package pl.arekbednarz.coupons.adapter.out.persistence.mapper;

import org.mapstruct.*;
import pl.arekbednarz.coupons.adapter.out.persistence.entity.CouponEntity;
import pl.arekbednarz.coupons.domain.model.Coupon;

@Mapper(componentModel = "spring")
public interface CouponMapper {

    @Mapping(target = "code", expression = "java(coupon.code().value())")
    @Mapping(target = "countryCode", expression = "java(coupon.countryCode().value())")
    CouponEntity toEntity(Coupon coupon);

    @Mapping(target = "code", expression = "java(CouponCode.of(entity.getCode()))")
    @Mapping(target = "countryCode", expression = "java(CountryCode.of(entity.getCountryCode()))")
    Coupon toDomain(CouponEntity entity);
}
