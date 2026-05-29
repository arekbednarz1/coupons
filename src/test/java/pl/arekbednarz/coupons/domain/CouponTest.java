package pl.arekbednarz.coupons.domain;

import org.junit.jupiter.api.Test;
import pl.arekbednarz.coupons.domain.exception.CouponExhaustedException;
import pl.arekbednarz.coupons.domain.exception.CouponForbiddenCountryException;
import pl.arekbednarz.coupons.domain.model.Coupon;
import pl.arekbednarz.coupons.domain.model.value.CountryCode;
import pl.arekbednarz.coupons.domain.model.value.CouponCode;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;


class CouponTest {

	@Test
	void shouldCreateValidCoupon() {
		UUID id = UUID.randomUUID();
		Coupon coupon = new Coupon(
			id,
			CouponCode.of("ABC123"),
			Instant.now(),
			5,
			0,
			CountryCode.of("PL"),
			0L);

		assertThat(coupon.id()).isEqualTo(id);
		assertThat(coupon.code().value()).isEqualTo("ABC123");
		assertThat(coupon.maxUsages()).isEqualTo(5);
		assertThat(coupon.currentUsages()).isEqualTo(0);
		assertThat(coupon.countryCode().value()).isEqualTo("PL");
		assertThat(coupon.version()).isEqualTo(0L);
	}

	@Test
	void shouldRejectInvalidMaxUsages() {
		assertThatThrownBy(() -> new Coupon(
			UUID.randomUUID(),
			CouponCode.of("ABC"),
			Instant.now(),
			0,
			0,
			CountryCode.of("PL"),
			0L)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("maxUsages must be > 0");
	}

	@Test
	void shouldRejectInvalidCurrentUsages() {
		assertThatThrownBy(() -> new Coupon(
			UUID.randomUUID(),
			CouponCode.of("ABC"),
			Instant.now(),
			5,
			6,
			CountryCode.of("PL"),
			0L)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Invalid currentUsages");
	}

	@Test
	void shouldIncreaseUsageAndVersion() {
		Coupon coupon = new Coupon(
			UUID.randomUUID(),
			CouponCode.of("ABC123"),
			Instant.now(),
			5,
			2,
			CountryCode.of("PL"),
			3L);

		Coupon updated = coupon.useOnce("UserId", "8.8.8.8").updatedCoupon();

		assertThat(updated.currentUsages()).isEqualTo(3);
		assertThat(updated.version()).isEqualTo(3L);
	}

	@Test
	void shouldThrowWhenExhausted() {
		Coupon coupon = new Coupon(
			UUID.randomUUID(),
			CouponCode.of("ABC123"),
			Instant.now(),
			3,
			3,
			CountryCode.of("PL"),
			1L);

		assertThatThrownBy(() -> coupon.useOnce("UserId", "8.8.8.8"))
			.isInstanceOf(CouponExhaustedException.class)
			.hasMessage("Coupon ABC123 has no remaining usages");
	}

	@Test
	void shouldValidateCorrectCountry() {
		Coupon coupon = new Coupon(
			UUID.randomUUID(),
			CouponCode.of("ABC123"),
			Instant.now(),
			5,
			0,
			CountryCode.of("PL"),
			0L);

		assertThatCode(() -> coupon.validateCountry(CountryCode.of("PL")))
			.doesNotThrowAnyException();
	}

	@Test
	void shouldRejectForbiddenCountry() {
		Coupon coupon = new Coupon(
			UUID.randomUUID(),
			CouponCode.of("ABC123"),
			Instant.now(),
			5,
			0,
			CountryCode.of("PL"),
			0L);

		assertThatThrownBy(() -> coupon.validateCountry(CountryCode.of("DE")))
			.isInstanceOf(CouponForbiddenCountryException.class)
			.hasMessage("Coupon ABC123 cannot be used from country DE");
	}

	@Test
	void shouldCreateNewCouponWithDefaults() {
		Coupon coupon = Coupon.createNew("XYZ999", 10, "PL");

		assertThat(coupon.code().value()).isEqualTo("XYZ999");
		assertThat(coupon.maxUsages()).isEqualTo(10);
		assertThat(coupon.currentUsages()).isEqualTo(0);
		assertThat(coupon.countryCode().value()).isEqualTo("PL");
		assertThat(coupon.version()).isEqualTo(0L);
		assertThat(coupon.createdAt()).isNotNull();
		assertThat(coupon.id()).isNotNull();
	}
}
