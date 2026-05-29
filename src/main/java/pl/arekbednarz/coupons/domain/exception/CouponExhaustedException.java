package pl.arekbednarz.coupons.domain.exception;

public class CouponExhaustedException extends RuntimeException {
	public CouponExhaustedException(String code) {
		super("Coupon %s has no remaining usages".formatted(code));
	}
}
