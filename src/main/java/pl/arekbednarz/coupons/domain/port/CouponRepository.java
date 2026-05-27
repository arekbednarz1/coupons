package pl.arekbednarz.coupons.domain.port;

import pl.arekbednarz.coupons.domain.model.Coupon;
import pl.arekbednarz.coupons.domain.model.value.CouponCode;

import java.util.Optional;
import java.util.UUID;

public interface CouponRepository extends Repository<Coupon, UUID> {

    Optional<Coupon> findByCode(CouponCode code);
}
