package pl.arekbednarz.coupons.adapter.out.persistence.mapper;

import org.mapstruct.*;
import pl.arekbednarz.coupons.adapter.out.persistence.entity.CouponUsageEntity;
import pl.arekbednarz.coupons.domain.model.CouponUsage;

@Mapper(componentModel = "spring")
public interface CouponUsageMapper {

    @Mapping(target = "countryCode", expression = "java(usage.countryCode().value())")
    CouponUsageEntity toEntity(CouponUsage usage);

    @Mapping(target = "countryCode", expression = "java(CountryCode.of(entity.getCountryCode()))")
    CouponUsage toDomain(CouponUsageEntity entity);
}

