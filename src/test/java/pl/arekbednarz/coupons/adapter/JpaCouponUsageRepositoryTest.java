package pl.arekbednarz.coupons.adapter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import pl.arekbednarz.coupons.adapter.out.persistence.repository.JpaCouponUsageRepository;
import pl.arekbednarz.coupons.domain.model.CouponUsage;
import pl.arekbednarz.coupons.domain.model.value.CountryCode;
import pl.arekbednarz.coupons.utils.PostgresqlTestContainer;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@ContextConfiguration(initializers = {
	PostgresqlTestContainer.class
})
class JpaCouponUsageRepositoryTest {

	@Autowired
	private JpaCouponUsageRepository repository;

	private CouponUsage newUsage(UUID couponId) {
		return new CouponUsage(
			UUID.randomUUID(),
			couponId,
			"test",
			"127.0.0.1",
			CountryCode.of("PL"),
			Instant.now());
	}

	@Test
	void shouldSaveAndLoadUsage() {
		var couponId = UUID.randomUUID();
		var usage = newUsage(couponId);

		var saved = repository.save(usage);

		assertThat(saved.id()).isEqualTo(usage.id());
		assertThat(saved.couponId()).isEqualTo(couponId);
		assertThat(saved.userId()).isEqualTo("test");
		assertThat(saved.ipAddress()).isEqualTo("127.0.0.1");
		assertThat(saved.countryCode().value()).isEqualTo("PL");

		var loaded = repository.findById(usage.id());
		assertThat(loaded).isPresent();
		assertThat(loaded.get().userId()).isEqualTo("test");
	}

	@Test
	void shouldCheckExistsByCouponIdAndUserId() {

		var couponId = UUID.randomUUID();
		var usage = newUsage(couponId);
		repository.save(usage);

		var exists = repository.existsByCouponIdAndUserId(couponId, "test");

		assertThat(exists).isTrue();
	}

	@Test
	void shouldReturnFalseWhenUsageDoesNotExist() {
		var exists = repository.existsByCouponIdAndUserId(UUID.randomUUID(), "NOOBUSER");

		assertThat(exists).isFalse();
	}
}
