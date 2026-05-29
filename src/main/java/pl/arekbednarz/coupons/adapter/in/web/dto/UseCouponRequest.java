package pl.arekbednarz.coupons.adapter.in.web.dto;

import lombok.Data;


@Data
public class UseCouponRequest {
	private String userId;
	private String ipAddress;
}
