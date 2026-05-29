package pl.arekbednarz.coupons.domain.exception;

public class CouponForbiddenCountryException extends RuntimeException {
	public CouponForbiddenCountryException(String code, String country) {
		super("Coupon %s cannot be used from country %s".formatted(code, country));
	}
}
