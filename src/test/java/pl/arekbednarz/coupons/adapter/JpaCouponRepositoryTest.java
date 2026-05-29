package pl.arekbednarz.coupons.adapter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ContextConfiguration;
import pl.arekbednarz.coupons.adapter.out.persistence.mapper.CouponMapper;
import pl.arekbednarz.coupons.adapter.out.persistence.repository.JpaCouponRepository;
import pl.arekbednarz.coupons.adapter.out.persistence.repository.SpringDataCouponJpa;
import pl.arekbednarz.coupons.domain.model.Coupon;
import pl.arekbednarz.coupons.domain.model.value.CountryCode;
import pl.arekbednarz.coupons.domain.model.value.CouponCode;
import pl.arekbednarz.coupons.utils.PostgresqlTestContainer;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@ContextConfiguration(initializers = {
	PostgresqlTestContainer.class
})
class JpaCouponRepositoryTest {

	@Autowired
	private JpaCouponRepository repository;

	@Autowired
	private SpringDataCouponJpa jpa;

	@Autowired
	private CouponMapper mapper;

	private Coupon newCoupon(String code) {
		return new Coupon(
			UUID.randomUUID(),
			CouponCode.of(code),
			Instant.now(),
			5,
			0,
			CountryCode.of("PL"),
			0L);
	}

	@Test
	void shouldSaveAndLoadCoupon() {
		var coupon = newCoupon("ABC123");

		var saved = repository.save(coupon);

		assertThat(saved.id()).isEqualTo(coupon.id());
		assertThat(saved.code().value()).isEqualTo("ABC123");
		assertThat(saved.currentUsages()).isZero();
		assertThat(saved.maxUsages()).isEqualTo(5);
		assertThat(saved.countryCode().value()).isEqualTo("PL");

		var loaded = repository.findById(coupon.id());
		assertThat(loaded).isPresent();
		assertThat(loaded.get().code().value()).isEqualTo("ABC123");
	}

	@Test
	void shouldFindByCode() {
		var coupon = newCoupon("XYZ999");
		repository.save(coupon);

		var found = repository.findByCode(CouponCode.of("XYZ999"));

		assertThat(found).isPresent();
		assertThat(found.get().code().value()).isEqualTo("XYZ999");
	}

	@Test
	void shouldThrowOptimisticLockingException() {
		var coupon = newCoupon("LOCK1");
		var entity = mapper.toEntity(coupon);
		var saved = jpa.save(entity);

		var fresh = jpa.findById(saved.getId()).orElseThrow();

		fresh.setCurrentUsages(fresh.getCurrentUsages() + 1);
		jpa.save(fresh);

		var stale = jpa.findById(saved.getId()).orElseThrow();
		stale.setCurrentUsages(stale.getCurrentUsages() + 1);
		stale.setVersion(saved.getVersion());

		assertThatThrownBy(() -> jpa.save(stale))
			.isInstanceOf(ObjectOptimisticLockingFailureException.class);
	}

}
