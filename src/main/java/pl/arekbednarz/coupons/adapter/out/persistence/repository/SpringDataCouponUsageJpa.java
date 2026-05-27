package pl.arekbednarz.coupons.adapter.out.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.arekbednarz.coupons.adapter.out.persistence.entity.CouponUsageEntity;

import java.util.UUID;

public interface SpringDataCouponUsageJpa extends JpaRepository<CouponUsageEntity, UUID> {

    boolean existsByCouponIdAndUserId(UUID couponId, String userId);
}
