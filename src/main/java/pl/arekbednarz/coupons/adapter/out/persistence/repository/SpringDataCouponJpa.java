package pl.arekbednarz.coupons.adapter.out.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.arekbednarz.coupons.adapter.out.persistence.entity.CouponEntity;

import java.util.Optional;
import java.util.UUID;


public interface SpringDataCouponJpa extends JpaRepository<CouponEntity, UUID> {

	Optional<CouponEntity> findByCode(String code);
}
