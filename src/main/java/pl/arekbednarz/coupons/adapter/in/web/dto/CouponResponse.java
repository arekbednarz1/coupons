package pl.arekbednarz.coupons.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;


@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class CouponResponse {
	String id;
	String code;
	int maxUsages;
	int currentUsages;
	String countryCode;
}
