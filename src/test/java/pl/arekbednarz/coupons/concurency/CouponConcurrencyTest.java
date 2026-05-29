package pl.arekbednarz.coupons.concurency;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import pl.arekbednarz.coupons.adapter.out.persistence.entity.CouponEntity;
import pl.arekbednarz.coupons.adapter.out.persistence.repository.SpringDataCouponJpa;
import pl.arekbednarz.coupons.application.service.CouponService;
import pl.arekbednarz.coupons.application.service.UseCouponResult;
import pl.arekbednarz.coupons.domain.port.GeoLocationService;
import pl.arekbednarz.coupons.utils.PostgresqlTestContainer;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


@SpringBootTest
@ContextConfiguration(initializers = PostgresqlTestContainer.class)
class CouponConcurrencyTest {

	@Autowired
	CouponService service;

	@Autowired
	SpringDataCouponJpa jpa;

	@MockitoBean
	GeoLocationService geoLocationService;

	@Test
	void shouldHandleConcurrentCouponUsageWithRetry() throws Exception {
		when(geoLocationService.resolveCountryCode(anyString()))
			.thenReturn(Optional.of("PL"));
		CouponEntity entity = new CouponEntity();
		entity.setId(UUID.randomUUID());
		entity.setCode("CONC123");
		entity.setCreatedAt(Instant.now());
		entity.setMaxUsages(5);
		entity.setCurrentUsages(0);
		entity.setCountryCode("PL");
		entity.setVersion(0L);

		CouponService spy = Mockito.spy(service);

		when(geoLocationService.resolveCountryCode(anyString()))
			.thenReturn(Optional.of("PL"));

		jpa.save(entity);

		ExecutorService executor = Executors.newFixedThreadPool(2);

		Callable<UseCouponResult> task = () -> spy.useCoupon("CONC123", UUID.randomUUID().toString(), "196.247.180.132");

		Future<UseCouponResult> f1 = executor.submit(task);
		Future<UseCouponResult> f2 = executor.submit(task);

		UseCouponResult r1 = f1.get();
		UseCouponResult r2 = f2.get();

		assertThat(r1.remainingUsages()).isIn(3, 4);
		assertThat(r2.remainingUsages()).isIn(3, 4);
		assertThat(r1.remainingUsages()).isNotEqualTo(r2.remainingUsages());

		CouponEntity finalState = jpa.findByCode("CONC123").orElseThrow();
		assertThat(finalState.getCurrentUsages()).isEqualTo(2);

		executor.shutdown();
	}
}
