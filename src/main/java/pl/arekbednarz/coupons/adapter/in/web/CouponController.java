package pl.arekbednarz.coupons.adapter.in.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.arekbednarz.coupons.adapter.in.web.dto.*;
import pl.arekbednarz.coupons.application.service.CouponService;
import pl.arekbednarz.coupons.domain.model.Coupon;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/coupons")
public class CouponController {

	private final CouponService couponService;

	@PostMapping()
	public ResponseEntity<CouponResponse> create(@RequestBody @Valid CreateCouponRequest request) {

		Coupon coupon = couponService.createCoupon(
			request.getCode(),
			request.getMaxUsages(),
			request.getCountryCode());

		CouponResponse response = CouponResponse.builder()
			.id(coupon.id().toString())
			.code(coupon.code().value())
			.maxUsages(coupon.maxUsages())
			.currentUsages(coupon.currentUsages())
			.countryCode(coupon.countryCode().value())
			.build();

		return ResponseEntity.status(201).body(response);
	}

	@PostMapping("/{code}/use")
	public ResponseEntity<UseCouponResponse> use(
		@PathVariable String code,
		@RequestParam("userId") @NotBlank String userId,
		@RequestHeader(value = "X-Real-IP", required = false) String ip) {
		var result = couponService.useCoupon(
			code,
			userId,
			ip);

		return ResponseEntity.ok(
			UseCouponResponse.builder()
				.couponCode(result.couponCode())
				.remainingUsages(result.remainingUsages())
				.build());
	}
}
