package pl.arekbednarz.coupons.domain.exception;

public class CouponConcurrentUsageException extends RuntimeException {
    public CouponConcurrentUsageException(String code) {
        super("Concurrent usage detected for coupon '%s'".formatted(code));
    }
}
