package pl.arekbednarz.coupons.application.service;

import jakarta.persistence.OptimisticLockException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.arekbednarz.coupons.domain.exception.*;
import pl.arekbednarz.coupons.domain.model.Coupon;
import pl.arekbednarz.coupons.domain.model.value.CountryCode;
import pl.arekbednarz.coupons.domain.model.value.CouponCode;
import pl.arekbednarz.coupons.domain.port.CouponRepository;
import pl.arekbednarz.coupons.domain.port.CouponUsageRepository;
import pl.arekbednarz.coupons.domain.port.GeoLocationService;


@Service
public class CouponService {

	private final int maxRetries;
	private final CouponRepository couponRepository;
	private final CouponUsageRepository usageRepository;
	private final GeoLocationService geoLocationService;
	private final CouponUsageEventHandler usageEventHandler;

	public CouponService(CouponRepository couponRepository,
		CouponUsageRepository usageRepository,
		GeoLocationService geoLocationService,
		CouponUsageEventHandler usageEventHandler,
		@Value("${max.retries:3}") int maxRetries) {
		this.couponRepository = couponRepository;
		this.usageRepository = usageRepository;
		this.geoLocationService = geoLocationService;
		this.usageEventHandler = usageEventHandler;
		this.maxRetries = maxRetries;
	}

	@Transactional
	public Coupon createCoupon(String rawCode, int maxUsages, String rawCountry) {
		Coupon coupon = Coupon.createNew(rawCode, maxUsages, rawCountry);
		return couponRepository.save(coupon);
	}

	public UseCouponResult useCoupon(String rawCode, String userId, String ipAddress) {

		CouponCode code = CouponCode.of(rawCode);

		Coupon coupon = couponRepository.findByCode(code)
			.orElseThrow(() -> new CouponNotFoundException(code.value()));

		CountryCode userCountry = resolveUserCountry(ipAddress);
		coupon.validateCountry(userCountry);

		validateUserNotUsedBefore(coupon, userId);

		int attempts = 0;

		while (true) {
			try {
				Coupon.CouponUsedResult result = coupon.useOnce(userId, ipAddress);

				Coupon persisted = couponRepository.save(result.updatedCoupon());

				usageEventHandler.handle(result.event());

				int remaining = persisted.maxUsages() - persisted.currentUsages();
				return new UseCouponResult(persisted.code().value(), remaining);

			} catch (ObjectOptimisticLockingFailureException
				| OptimisticLockException e) {

				if (++attempts >= maxRetries) {
					throw new CouponConcurrentUsageException(code.value());
				}

				coupon = couponRepository.findByCode(code)
					.orElseThrow(() -> new CouponNotFoundException(code.value()));
			}
		}
	}

	private CountryCode resolveUserCountry(String ipAddress) {
		String country = geoLocationService.resolveCountryCode(ipAddress)
			.orElseThrow(() -> new GeoLocationUnavailableException(ipAddress));
		return CountryCode.of(country);
	}

	private void validateUserNotUsedBefore(Coupon coupon, String userId) {
		if (userId != null &&
			usageRepository.existsByCouponIdAndUserId(coupon.id(), userId)) {
			throw new CouponAlreadyUsedByUserException(coupon.code().value(), userId);
		}
	}
}
