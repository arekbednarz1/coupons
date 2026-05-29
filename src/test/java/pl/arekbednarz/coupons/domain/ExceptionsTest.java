package pl.arekbednarz.coupons.domain;

import org.junit.jupiter.api.Test;
import pl.arekbednarz.coupons.domain.exception.*;

import static org.assertj.core.api.Assertions.*;


class ExceptionsTest {

	@Test
	void testCouponExhaustedException() {
		assertThat(new CouponExhaustedException("ABC").getMessage())
			.contains("ABC");
	}

	@Test
	void testCouponForbiddenCountryException() {
		assertThat(new CouponForbiddenCountryException("ABC", "DE").getMessage())
			.contains("ABC")
			.contains("DE");
	}

	@Test
	void testCouponNotFoundException() {
		assertThat(new CouponNotFoundException("ABC").getMessage())
			.contains("ABC");
	}

	@Test
	void testCouponAlreadyUsedByUserException() {
		assertThat(new CouponAlreadyUsedByUserException("ABC", "arek").getMessage())
			.contains("ABC")
			.contains("arek");
	}

	@Test
	void testGeoLocationUnavailableException() {
		assertThat(new GeoLocationUnavailableException("1.1.1.1").getMessage())
			.contains("1.1.1.1");
	}

	@Test
	void testCouponConcurrentUsageException() {
		assertThat(new CouponConcurrentUsageException("ABC").getMessage())
			.contains("ABC");
	}
}
