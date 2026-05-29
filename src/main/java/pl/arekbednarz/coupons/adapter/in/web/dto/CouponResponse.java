package pl.arekbednarz.coupons.adapter.in.web.dto;

import lombok.Builder;
import lombok.Value;


@Value
@Builder
public class CouponResponse {
	String id;
	String code;
	int maxUsages;
	int currentUsages;
	String countryCode;
}
