package pl.arekbednarz.coupons.domain;

import org.junit.jupiter.api.Test;
import pl.arekbednarz.coupons.domain.model.CouponUsage;
import pl.arekbednarz.coupons.domain.model.value.CountryCode;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;


class CouponUsageTest {

	@Test
	void shouldCreateValidUsage() {
		UUID id = UUID.randomUUID();
		UUID couponId = UUID.randomUUID();
		Instant now = Instant.now();

		CouponUsage usage = new CouponUsage(
			id,
			couponId,
			"test",
			"8.8.8.8",
			CountryCode.of("PL"),
			now);

		assertThat(usage.id()).isEqualTo(id);
		assertThat(usage.couponId()).isEqualTo(couponId);
		assertThat(usage.userId()).isEqualTo("test");
		assertThat(usage.ipAddress()).isEqualTo("8.8.8.8");
		assertThat(usage.countryCode().value()).isEqualTo("PL");
		assertThat(usage.usedAt()).isEqualTo(now);
	}

	@Test
	void shouldRejectNullArguments() {
		UUID id = UUID.randomUUID();
		UUID couponId = UUID.randomUUID();
		Instant now = Instant.now();

		assertThatThrownBy(() -> new CouponUsage(null, couponId, "test", "8.8.8.8", CountryCode.of("PL"), now)).isInstanceOf(NullPointerException.class);

		assertThatThrownBy(() -> new CouponUsage(id, null, "test", "8.8.8.8", CountryCode.of("PL"), now)).isInstanceOf(NullPointerException.class);

		assertThatThrownBy(() -> new CouponUsage(id, couponId, null, "8.8.8.8", CountryCode.of("PL"), now)).isInstanceOf(NullPointerException.class);

		assertThatThrownBy(() -> new CouponUsage(id, couponId, "test", null, CountryCode.of("PL"), now)).isInstanceOf(NullPointerException.class);

		assertThatThrownBy(() -> new CouponUsage(id, couponId, "test", "8.8.8.8", null, now)).isInstanceOf(NullPointerException.class);

		assertThatThrownBy(() -> new CouponUsage(id, couponId, "test", "8.8.8.8", CountryCode.of("PL"), null)).isInstanceOf(NullPointerException.class);
	}

	@Test
	void shouldCreateNewUsageWithGeneratedIdAndTimestamp() {
		UUID couponId = UUID.randomUUID();

		CouponUsage usage = CouponUsage.createNew(
			couponId,
			"test",
			"8.8.8.8",
			"PL");

		assertThat(usage.id()).isNotNull();
		assertThat(usage.couponId()).isEqualTo(couponId);
		assertThat(usage.userId()).isEqualTo("test");
		assertThat(usage.ipAddress()).isEqualTo("8.8.8.8");
		assertThat(usage.countryCode().value()).isEqualTo("PL");
		assertThat(usage.usedAt()).isNotNull();
		assertThat(usage.usedAt()).isBeforeOrEqualTo(Instant.now());
	}
}
