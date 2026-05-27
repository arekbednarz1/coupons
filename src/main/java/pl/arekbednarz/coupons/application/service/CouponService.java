package pl.arekbednarz.coupons.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.arekbednarz.coupons.domain.exception.*;
import pl.arekbednarz.coupons.domain.model.Coupon;
import pl.arekbednarz.coupons.domain.model.CouponUsage;
import pl.arekbednarz.coupons.domain.model.value.CountryCode;
import pl.arekbednarz.coupons.domain.model.value.CouponCode;
import pl.arekbednarz.coupons.domain.port.CouponRepository;
import pl.arekbednarz.coupons.domain.port.CouponUsageRepository;
import pl.arekbednarz.coupons.domain.port.GeoLocationService;

@Service
public class CouponService {

    private static final int MAX_RETRIES = 3;

    private final CouponRepository couponRepository;
    private final CouponUsageRepository couponUsageRepository;
    private final GeoLocationService geoLocationService;

    public CouponService(CouponRepository couponRepository,
                         CouponUsageRepository couponUsageRepository,
                         GeoLocationService geoLocationService) {
        this.couponRepository = couponRepository;
        this.couponUsageRepository = couponUsageRepository;
        this.geoLocationService = geoLocationService;
    }

    @Transactional
    public Coupon createCoupon(String rawCode, int maxUsages, String rawCountryCode) {
        Coupon coupon = Coupon.createNew(rawCode, maxUsages, rawCountryCode);
        return couponRepository.save(coupon);
    }

    @Transactional
    public UseCouponResult useCoupon(String rawCode, String userId, String ipAddress) {
        CouponCode code = CouponCode.of(rawCode);

        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new CouponNotFoundException(code.value()));

        String country = geoLocationService.resolveCountryCode(ipAddress)
                .orElseThrow(() -> new GeoLocationUnavailableException(ipAddress));

        CountryCode userCountry = CountryCode.of(country);
        coupon.validateCountry(userCountry);

        if (userId != null &&
                couponUsageRepository.existsByCouponIdAndUserId(coupon.id(), userId)) {
            throw new CouponAlreadyUsedByUserException(code.value(), userId);
        }

        int attempts = 0;
        while (true) {
            try {
                Coupon updated = coupon.useOnce();
                Coupon persisted = couponRepository.save(updated);

                CouponUsage usage = CouponUsage.createNew(
                        persisted.id(),
                        userId,
                        ipAddress,
                        persisted.countryCode().value()
                );
                couponUsageRepository.save(usage);

                int remaining = persisted.maxUsages() - persisted.currentUsages();
                return new UseCouponResult(persisted.code().value(), remaining);

            } catch (OptimisticLockingException ex) {
                if (++attempts >= MAX_RETRIES) {
                    throw new CouponConcurrentUsageException(code.value());
                }
                coupon = couponRepository.findByCode(code)
                        .orElseThrow(() -> new CouponNotFoundException(code.value()));
            }
        }
    }
}
