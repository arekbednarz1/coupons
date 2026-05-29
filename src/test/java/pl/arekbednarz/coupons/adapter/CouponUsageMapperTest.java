package pl.arekbednarz.coupons.adapter;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pl.arekbednarz.coupons.adapter.out.persistence.entity.CouponUsageEntity;
import pl.arekbednarz.coupons.adapter.out.persistence.mapper.CouponUsageMapper;
import pl.arekbednarz.coupons.domain.model.CouponUsage;
import pl.arekbednarz.coupons.domain.model.value.CountryCode;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


class CouponUsageMapperTest {

	private final CouponUsageMapper mapper = Mappers.getMapper(CouponUsageMapper.class);

	@Test
	void shouldMapDomainToEntity() {
		var usage = new CouponUsage(
			UUID.randomUUID(),
			UUID.randomUUID(),
			"arek",
			"127.0.0.1",
			CountryCode.of("PL"),
			Instant.now());

		var entity = mapper.toEntity(usage);

		assertThat(entity.getId()).isEqualTo(usage.id());
		assertThat(entity.getCouponId()).isEqualTo(usage.couponId());
		assertThat(entity.getUserId()).isEqualTo("arek");
		assertThat(entity.getIpAddress()).isEqualTo("127.0.0.1");
		assertThat(entity.getCountryCode()).isEqualTo("PL");
		assertThat(entity.getUsedAt()).isEqualTo(usage.usedAt());
	}

	@Test
	void shouldMapEntityToDomain() {
		var entity = new CouponUsageEntity(
			UUID.randomUUID(),
			UUID.randomUUID(),
			"john",
			"10.0.0.1",
			"US",
			Instant.now());

		var domain = mapper.toDomain(entity);

		assertThat(domain.id()).isEqualTo(entity.getId());
		assertThat(domain.couponId()).isEqualTo(entity.getCouponId());
		assertThat(domain.userId()).isEqualTo("john");
		assertThat(domain.ipAddress()).isEqualTo("10.0.0.1");
		assertThat(domain.countryCode().value()).isEqualTo("US");
		assertThat(domain.usedAt()).isEqualTo(entity.getUsedAt());
	}
}
