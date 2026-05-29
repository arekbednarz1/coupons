package pl.arekbednarz.coupons.application;

import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import pl.arekbednarz.coupons.application.service.CouponService;
import pl.arekbednarz.coupons.application.service.CouponUsageEventHandler;
import pl.arekbednarz.coupons.domain.event.CouponUsedEvent;
import pl.arekbednarz.coupons.domain.exception.*;
import pl.arekbednarz.coupons.domain.model.Coupon;
import pl.arekbednarz.coupons.domain.model.value.CountryCode;
import pl.arekbednarz.coupons.domain.model.value.CouponCode;
import pl.arekbednarz.coupons.domain.port.CouponRepository;
import pl.arekbednarz.coupons.domain.port.CouponUsageRepository;
import pl.arekbednarz.coupons.domain.port.GeoLocationService;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


class CouponServiceTest {

	private final CouponRepository couponRepository = mock(CouponRepository.class);
	private final CouponUsageRepository usageRepository = mock(CouponUsageRepository.class);
	private final GeoLocationService geoLocationService = mock(GeoLocationService.class);
	private final CouponUsageEventHandler usageEventHandler = mock(CouponUsageEventHandler.class);

	private final CouponService service =
		new CouponService(couponRepository, usageRepository, geoLocationService, usageEventHandler, 3);

	private Coupon sampleCoupon() {
		return new Coupon(
			UUID.randomUUID(),
			CouponCode.of("ABC123"),
			Instant.now(),
			5,
			0,
			CountryCode.of("PL"),
			0L);
	}

	@Test
	void shouldUseCouponSuccessfully() {
		Coupon coupon = sampleCoupon();

		when(couponRepository.findByCode(CouponCode.of("ABC123")))
			.thenReturn(Optional.of(coupon));

		when(geoLocationService.resolveCountryCode("8.8.8.8"))
			.thenReturn(Optional.of("PL"));

		when(couponRepository.save(any()))
			.thenAnswer(inv -> inv.getArgument(0));

		var result = service.useCoupon("ABC123", "arek", "8.8.8.8");

		assertThat(result.couponCode()).isEqualTo("ABC123");
		assertThat(result.remainingUsages()).isEqualTo(4);

		verify(usageEventHandler).handle(any(CouponUsedEvent.class));
	}

	@Test
	void shouldRetryOnOptimisticLock() {
		Coupon coupon = sampleCoupon();

		when(couponRepository.findByCode(CouponCode.of("ABC123")))
			.thenReturn(Optional.of(coupon));

		when(geoLocationService.resolveCountryCode("8.8.8.8"))
			.thenReturn(Optional.of("PL"));

		when(couponRepository.save(any()))
			.thenThrow(new OptimisticLockException())
			.thenAnswer(inv -> inv.getArgument(0));

		var result = service.useCoupon("ABC123", "arek", "8.8.8.8");

		assertThat(result.remainingUsages()).isEqualTo(4);

		InOrder order = inOrder(couponRepository, usageEventHandler);
		order.verify(couponRepository, times(2)).save(any());
		order.verify(usageEventHandler).handle(any(CouponUsedEvent.class));
	}

	@Test
	void shouldFailAfterMaxRetries() {
		Coupon coupon = sampleCoupon();

		when(couponRepository.findByCode(CouponCode.of("ABC123")))
			.thenReturn(Optional.of(coupon));

		when(geoLocationService.resolveCountryCode("8.8.8.8"))
			.thenReturn(Optional.of("PL"));

		when(couponRepository.save(any()))
			.thenThrow(new OptimisticLockException());

		assertThatThrownBy(() -> service.useCoupon("ABC123", "arek", "8.8.8.8")).isInstanceOf(CouponConcurrentUsageException.class);
	}

	@Test
	void shouldThrowWhenCouponNotFound() {
		when(couponRepository.findByCode(any()))
			.thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.useCoupon("ABC123", "arek", "8.8.8.8")).isInstanceOf(CouponNotFoundException.class);
	}

	@Test
	void shouldThrowWhenGeoLocationUnavailable() {
		Coupon coupon = sampleCoupon();

		when(couponRepository.findByCode(any()))
			.thenReturn(Optional.of(coupon));

		when(geoLocationService.resolveCountryCode(anyString()))
			.thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.useCoupon("ABC123", "arek", "8.8.8.8")).isInstanceOf(GeoLocationUnavailableException.class);
	}

	@Test
	void shouldThrowWhenForbiddenCountry() {
		Coupon coupon = sampleCoupon();

		when(couponRepository.findByCode(any()))
			.thenReturn(Optional.of(coupon));

		when(geoLocationService.resolveCountryCode(anyString()))
			.thenReturn(Optional.of("DE"));

		assertThatThrownBy(() -> service.useCoupon("ABC123", "arek", "8.8.8.8")).isInstanceOf(CouponForbiddenCountryException.class);
	}

	@Test
	void shouldThrowWhenUserAlreadyUsedCoupon() {
		Coupon coupon = sampleCoupon();

		when(couponRepository.findByCode(any()))
			.thenReturn(Optional.of(coupon));

		when(geoLocationService.resolveCountryCode(anyString()))
			.thenReturn(Optional.of("PL"));

		when(usageRepository.existsByCouponIdAndUserId(any(), any()))
			.thenReturn(true);

		assertThatThrownBy(() -> service.useCoupon("ABC123", "arek", "8.8.8.8")).isInstanceOf(CouponAlreadyUsedByUserException.class);
	}

	@Test
	void shouldCreateCoupon() {
		Coupon saved = new Coupon(
			UUID.randomUUID(),
			CouponCode.of("NEW123"),
			Instant.now(),
			10,
			0,
			CountryCode.of("PL"),
			0L);

		when(couponRepository.save(any())).thenReturn(saved);

		Coupon result = service.createCoupon("NEW123", 10, "PL");

		assertThat(result.code().value()).isEqualTo("NEW123");
		assertThat(result.maxUsages()).isEqualTo(10);
		assertThat(result.countryCode().value()).isEqualTo("PL");

		verify(couponRepository).save(any(Coupon.class));
	}

}
