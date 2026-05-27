package pl.arekbednarz.coupons.domain.port;

import pl.arekbednarz.coupons.domain.model.CouponUsage;

import java.util.UUID;

public interface CouponUsageRepository extends Repository<CouponUsage, UUID> {

    boolean existsByCouponIdAndUserId(UUID couponId, String userId);
}
