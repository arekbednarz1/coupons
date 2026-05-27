package pl.arekbednarz.coupons.adapter.in.web.dto;

import lombok.Data;

@Data
public class CreateCouponRequest {
    private String code;
    private int maxUsages;
    private String countryCode;
}
