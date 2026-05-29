package pl.arekbednarz.coupons.domain;

import org.junit.jupiter.api.Test;
import pl.arekbednarz.coupons.domain.model.value.CouponCode;

import static org.assertj.core.api.Assertions.*;


class CouponCodeTest {

	@Test
	void shouldCreateValidCouponCode() {
		CouponCode code = CouponCode.of("abc123");

		assertThat(code.value()).isEqualTo("ABC123");
		assertThat(code.toString()).isEqualTo("ABC123");
	}

	@Test
	void shouldTrimAndUppercaseInput() {
		CouponCode code = CouponCode.of("   xyz999   ");

		assertThat(code.value()).isEqualTo("XYZ999");
	}

	@Test
	void shouldRejectNull() {
		assertThatThrownBy(() -> CouponCode.of(null))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Coupon code must not be blank");
	}

	@Test
	void shouldRejectBlank() {
		assertThatThrownBy(() -> CouponCode.of("   "))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Coupon code must not be blank");
	}

	@Test
	void shouldImplementEqualsAndHashCodeCorrectly() {
		CouponCode a = CouponCode.of("abc");
		CouponCode b = CouponCode.of("ABC");

		assertThat(a).isEqualTo(b);
		assertThat(a.hashCode()).isEqualTo(b.hashCode());
	}

	@Test
	void shouldNotBeEqualToDifferentCode() {
		CouponCode a = CouponCode.of("AAA");
		CouponCode b = CouponCode.of("BBB");

		assertThat(a).isNotEqualTo(b);
	}
}
