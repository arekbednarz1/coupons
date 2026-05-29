package pl.arekbednarz.coupons.adapter;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pl.arekbednarz.coupons.adapter.out.persistence.entity.CouponEntity;
import pl.arekbednarz.coupons.adapter.out.persistence.mapper.CouponMapper;
import pl.arekbednarz.coupons.domain.model.Coupon;
import pl.arekbednarz.coupons.domain.model.value.CountryCode;
import pl.arekbednarz.coupons.domain.model.value.CouponCode;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


class CouponMapperTest {

	private final CouponMapper mapper = Mappers.getMapper(CouponMapper.class);

	@Test
	void shouldMapDomainToEntity() {
		var coupon = new Coupon(
			UUID.randomUUID(),
			CouponCode.of("ABC123"),
			Instant.now(),
			5,
			1,
			CountryCode.of("PL"),
			3L);

		var entity = mapper.toEntity(coupon);

		assertThat(entity.getId()).isEqualTo(coupon.id());
		assertThat(entity.getCode()).isEqualTo("ABC123");
		assertThat(entity.getCreatedAt()).isEqualTo(coupon.createdAt());
		assertThat(entity.getMaxUsages()).isEqualTo(5);
		assertThat(entity.getCurrentUsages()).isEqualTo(1);
		assertThat(entity.getCountryCode()).isEqualTo("PL");
		assertThat(entity.getVersion()).isEqualTo(3L);
	}

	@Test
	void shouldMapEntityToDomain() {
		var entity = new CouponEntity(
			UUID.randomUUID(),
			"XYZ999",
			Instant.now(),
			10,
			4,
			"US",
			7L);

		var domain = mapper.toDomain(entity);

		assertThat(domain.id()).isEqualTo(entity.getId());
		assertThat(domain.code().value()).isEqualTo("XYZ999");
		assertThat(domain.createdAt()).isEqualTo(entity.getCreatedAt());
		assertThat(domain.maxUsages()).isEqualTo(10);
		assertThat(domain.currentUsages()).isEqualTo(4);
		assertThat(domain.countryCode().value()).isEqualTo("US");
		assertThat(domain.version()).isEqualTo(7L);
	}
}
