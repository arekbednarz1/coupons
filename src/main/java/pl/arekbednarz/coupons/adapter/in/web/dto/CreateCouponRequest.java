package pl.arekbednarz.coupons.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class CreateCouponRequest {
	@NotBlank
	private String code;
	private int maxUsages;
	@NotBlank
	private String countryCode;
}
