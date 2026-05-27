package pl.arekbednarz.coupons.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.arekbednarz.coupons.adapter.in.web.dto.*;
import pl.arekbednarz.coupons.application.service.CouponService;
import pl.arekbednarz.coupons.domain.model.Coupon;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponService couponService;

    @PostMapping
    public ResponseEntity<CouponResponse> create(@RequestBody CreateCouponRequest request) {

        Coupon coupon = couponService.createCoupon(
                request.getCode(),
                request.getMaxUsages(),
                request.getCountryCode()
        );

        CouponResponse response = CouponResponse.builder()
                .id(coupon.id().toString())
                .code(coupon.code().value())
                .maxUsages(coupon.maxUsages())
                .currentUsages(coupon.currentUsages())
                .countryCode(coupon.countryCode().value())
                .build();

        return ResponseEntity
                .created(URI.create("/api/coupons/" + coupon.id()))
                .body(response);
    }

    @PostMapping("/{code}/use")
    public ResponseEntity<UseCouponResponse> use(
            @PathVariable String code,
            @RequestBody UseCouponRequest request
    ) {
        var result = couponService.useCoupon(
                code,
                request.getUserId(),
                request.getIpAddress()
        );

        return ResponseEntity.ok(
                UseCouponResponse.builder()
                        .couponCode(result.couponCode())
                        .remainingUsages(result.remainingUsages())
                        .build()
        );
    }
}
