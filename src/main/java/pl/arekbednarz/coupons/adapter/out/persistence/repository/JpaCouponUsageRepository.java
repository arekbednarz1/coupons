package pl.arekbednarz.coupons.adapter.out.persistence.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pl.arekbednarz.coupons.adapter.out.persistence.mapper.CouponUsageMapper;
import pl.arekbednarz.coupons.domain.model.CouponUsage;
import pl.arekbednarz.coupons.domain.port.CouponUsageRepository;

import java.util.Optional;
import java.util.UUID;


@Repository
@RequiredArgsConstructor
public class JpaCouponUsageRepository implements CouponUsageRepository {

	private final SpringDataCouponUsageJpa jpa;
	private final CouponUsageMapper mapper;

	@Override
	public Optional<CouponUsage> findById(UUID id) {
		return jpa.findById(id).map(mapper::toDomain);
	}

	@Override
	public CouponUsage save(CouponUsage usage) {
		var saved = jpa.save(mapper.toEntity(usage));
		return mapper.toDomain(saved);
	}

	@Override
	public boolean existsByCouponIdAndUserId(UUID couponId, String userId) {
		return jpa.existsByCouponIdAndUserId(couponId, userId);
	}
}
