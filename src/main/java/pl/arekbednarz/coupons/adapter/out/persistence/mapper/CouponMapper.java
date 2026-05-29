package pl.arekbednarz.coupons.adapter.out.persistence.mapper;

import org.mapstruct.*;
import pl.arekbednarz.coupons.adapter.out.persistence.entity.CouponEntity;
import pl.arekbednarz.coupons.domain.model.Coupon;


@Mapper(componentModel = "spring")
public interface CouponMapper {

	@Mapping(target = "code", expression = "java(coupon.code().value())")
	@Mapping(target = "countryCode", expression = "java(coupon.countryCode().value())")
	@Mapping(target = "id", expression = "java(coupon.id())")
	@Mapping(target = "createdAt", expression = "java(coupon.createdAt())")
	@Mapping(target = "maxUsages", expression = "java(coupon.maxUsages())")
	@Mapping(target = "currentUsages", expression = "java(coupon.currentUsages())")
	@Mapping(target = "version", expression = "java(coupon.version())")
	CouponEntity toEntity(Coupon coupon);

	@Mapping(target = "code", expression = "java(coupon.code().value())")
	@Mapping(target = "countryCode", expression = "java(coupon.countryCode().value())")
	@Mapping(target = "id", expression = "java(coupon.id())")
	@Mapping(target = "createdAt", expression = "java(coupon.createdAt())")
	@Mapping(target = "maxUsages", expression = "java(coupon.maxUsages())")
	@Mapping(target = "currentUsages", expression = "java(coupon.currentUsages())")
	@Mapping(target = "version", expression = "java(coupon.version())")
	void updateEntity(@MappingTarget CouponEntity entity, Coupon coupon);

	@Mapping(target = "code", expression = "java(CouponCode.of(entity.getCode()))")
	@Mapping(target = "countryCode", expression = "java(CountryCode.of(entity.getCountryCode()))")
	@Mapping(target = "version", source = "version")
	Coupon toDomain(CouponEntity entity);
}
