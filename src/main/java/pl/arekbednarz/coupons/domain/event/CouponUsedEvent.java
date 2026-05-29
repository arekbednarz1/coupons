package pl.arekbednarz.coupons.domain.event;

import java.time.Instant;
import java.util.UUID;


public record CouponUsedEvent(
	UUID couponId,
	String couponCode,
	String userId,
	String ipAddress,
	String countryCode,
	Instant occurredAt) {
}
