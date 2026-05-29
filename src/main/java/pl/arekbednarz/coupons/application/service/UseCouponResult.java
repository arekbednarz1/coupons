package pl.arekbednarz.coupons.application.service;

public record UseCouponResult(
	String couponCode,
	int remainingUsages) {
}
