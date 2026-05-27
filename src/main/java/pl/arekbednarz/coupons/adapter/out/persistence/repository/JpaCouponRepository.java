package pl.arekbednarz.coupons.adapter.out.persistence.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Repository;
import pl.arekbednarz.coupons.adapter.out.persistence.mapper.CouponMapper;
import pl.arekbednarz.coupons.domain.exception.OptimisticLockingException;
import pl.arekbednarz.coupons.domain.model.Coupon;
import pl.arekbednarz.coupons.domain.model.value.CouponCode;
import pl.arekbednarz.coupons.domain.port.CouponRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaCouponRepository implements CouponRepository {

    private final SpringDataCouponJpa jpa;
    private final CouponMapper mapper;

    @Override
    public Optional<Coupon> findById(UUID id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Coupon> findByCode(CouponCode code) {
        return jpa.findByCode(code.value()).map(mapper::toDomain);
    }

    @Override
    public Coupon save(Coupon coupon) {
        try {
            var saved = jpa.save(mapper.toEntity(coupon));
            return mapper.toDomain(saved);
        } catch (OptimisticLockingFailureException ex) {
            throw new OptimisticLockingException("Optimistic lock failed", ex);
        }
    }
}
