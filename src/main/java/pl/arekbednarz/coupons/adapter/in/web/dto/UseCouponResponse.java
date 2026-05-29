package pl.arekbednarz.coupons.adapter.in.web.dto;

import lombok.Builder;
import lombok.Value;


@Value
@Builder
public class UseCouponResponse {
	String couponCode;
	int remainingUsages;
}
