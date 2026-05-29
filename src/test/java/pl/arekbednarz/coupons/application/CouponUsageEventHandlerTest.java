package pl.arekbednarz.coupons.application;

import org.junit.jupiter.api.Test;
import pl.arekbednarz.coupons.application.service.CouponUsageEventHandler;
import pl.arekbednarz.coupons.domain.event.CouponUsedEvent;
import pl.arekbednarz.coupons.domain.model.CouponUsage;
import pl.arekbednarz.coupons.domain.port.CouponUsageRepository;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class CouponUsageEventHandlerTest {

	private final CouponUsageRepository usageRepository = mock(CouponUsageRepository.class);
	private final CouponUsageEventHandler handler = new CouponUsageEventHandler(usageRepository);

	@Test
	void shouldSaveUsageOnEvent() {
		CouponUsedEvent event = new CouponUsedEvent(
			UUID.randomUUID(),
			"ABC123",
			"arek",
			"8.8.8.8",
			"PL",
			Instant.now());

		handler.handle(event);

		verify(usageRepository).save(any(CouponUsage.class));
	}
}
