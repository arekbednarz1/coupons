package pl.arekbednarz.coupons.domain.exception;

public class CouponAlreadyUsedByUserException extends RuntimeException {
    public CouponAlreadyUsedByUserException(String code, String userId) {
        super("User '%s' already used coupon '%s'".formatted(userId, code));
    }
}
