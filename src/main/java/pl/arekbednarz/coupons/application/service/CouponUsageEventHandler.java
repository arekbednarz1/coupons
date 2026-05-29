package pl.arekbednarz.coupons.application.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.arekbednarz.coupons.domain.event.CouponUsedEvent;
import pl.arekbednarz.coupons.domain.model.CouponUsage;
import pl.arekbednarz.coupons.domain.port.CouponUsageRepository;


@Component
public class CouponUsageEventHandler {

	private final CouponUsageRepository usageRepository;

	public CouponUsageEventHandler(CouponUsageRepository usageRepository) {
		this.usageRepository = usageRepository;
	}

	@Transactional
	public void handle(CouponUsedEvent event) {
		CouponUsage usage = CouponUsage.createNew(
			event.couponId(),
			event.userId(),
			event.ipAddress(),
			event.countryCode());
		usageRepository.save(usage);
	}
}
