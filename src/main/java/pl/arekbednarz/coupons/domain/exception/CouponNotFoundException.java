package pl.arekbednarz.coupons.domain.exception;

public class CouponNotFoundException extends RuntimeException {
    public CouponNotFoundException(String code) {
        super("Coupon '%s' not found".formatted(code));
    }
}
