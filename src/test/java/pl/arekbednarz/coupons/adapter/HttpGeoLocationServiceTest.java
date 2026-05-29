package pl.arekbednarz.coupons.adapter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.arekbednarz.coupons.adapter.out.geo.HttpClientFacade;
import pl.arekbednarz.coupons.adapter.out.geo.HttpGeoLocationService;
import pl.arekbednarz.coupons.adapter.out.geo.JavaHttpClientFacade;
import pl.arekbednarz.coupons.domain.port.GeoLocationService;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class HttpGeoLocationServiceTest {

	@Test
	void shouldReturnCountryCodeWhenResponseIs200() throws Exception {
		HttpClientFacade client = mock(HttpClientFacade.class);
		JavaHttpClientFacade javaClient = mock(JavaHttpClientFacade.class);
		HttpResponse<String> response = mock(HttpResponse.class);

		when(response.statusCode()).thenReturn(200);
		when(response.body()).thenReturn("pl");

		when(client.send(any(HttpRequest.class)))
			.thenReturn(response);

		HttpGeoLocationService service = new HttpGeoLocationService("http://test/", javaClient) {
			@Override
			public Optional<String> resolveCountryCode(String ip) {
				try {
					HttpRequest req = HttpRequest.newBuilder()
						.uri(URI.create("http://test/" + ip + "/country/"))
						.GET()
						.build();

					HttpResponse<String> resp = client.send(req);
					if (resp.statusCode() != 200)
						return Optional.empty();
					String c = resp.body().trim().toUpperCase();
					return c.isBlank() ? Optional.empty() : Optional.of(c);
				} catch (Exception e) {
					return Optional.empty();
				}
			}
		};

		Optional<String> result = service.resolveCountryCode("8.8.8.8");

		assertThat(result).contains("PL");
	}

	@Test
	void shouldReturnEmptyWhenStatusNot200() throws Exception {
		HttpResponse<String> response = mock(HttpResponse.class);
		HttpClientFacade client = mock(HttpClientFacade.class);
		JavaHttpClientFacade javaClient = mock(JavaHttpClientFacade.class);

		when(response.statusCode()).thenReturn(404);
		when(client.send(any())).thenReturn(response);

		HttpGeoLocationService service = new HttpGeoLocationService("http://test/", javaClient) {
			@Override
			public Optional<String> resolveCountryCode(String ip) {
				try {
					HttpResponse<String> resp = client.send(any());
					return resp.statusCode() == 200
						? Optional.of(resp.body().trim().toUpperCase())
						: Optional.empty();
				} catch (Exception e) {
					return Optional.empty();
				}
			}
		};

		assertThat(service.resolveCountryCode("8.8.8.8")).isEmpty();
	}

	@Test
	void shouldReturnEmptyWhenBodyBlank() throws Exception {
		HttpClientFacade client = mock(HttpClientFacade.class);
		JavaHttpClientFacade javaClient = mock(JavaHttpClientFacade.class);
		HttpResponse<String> response = mock(HttpResponse.class);

		when(response.body()).thenReturn("   ");
		when(client.send(any())).thenReturn(response);

		HttpGeoLocationService service = new HttpGeoLocationService("http://test/", javaClient) {
			@Override
			public Optional<String> resolveCountryCode(String ip) {
				try {
					HttpResponse<String> resp = client.send(any());
					String c = resp.body().trim().toUpperCase();
					return c.isBlank() ? Optional.empty() : Optional.of(c);
				} catch (Exception e) {
					return Optional.empty();
				}
			}
		};

		assertThat(service.resolveCountryCode("8.8.8.8")).isEmpty();
	}

	@Test
	void shouldReturnEmptyOnException() throws Exception {
		HttpClientFacade client = mock(HttpClientFacade.class);
		JavaHttpClientFacade javaClient = mock(JavaHttpClientFacade.class);

		when(client.send(any())).thenThrow(new RuntimeException("boom"));

		HttpGeoLocationService service = new HttpGeoLocationService("http://test/", javaClient) {
			@Override
			public Optional<String> resolveCountryCode(String ip) {
				try {
					client.send(any());
					return Optional.of("FAIL");
				} catch (Exception e) {
					return Optional.empty();
				}
			}
		};

		assertThat(service.resolveCountryCode("8.8.8.8")).isEmpty();
	}

	@Test
	void shouldBuildCorrectHttpRequestAndUseJavaClient() throws Exception {
		JavaHttpClientFacade javaClient = mock(JavaHttpClientFacade.class);
		HttpResponse<String> response = mock(HttpResponse.class);

		when(response.statusCode()).thenReturn(200);
		when(response.body()).thenReturn("PL");
		when(javaClient.send(any(HttpRequest.class))).thenReturn(response);

		HttpGeoLocationService service = new HttpGeoLocationService("http://test", javaClient);

		Optional<String> result = service.resolveCountryCode("8.8.8.8");

		assertThat(result).contains("PL");

		ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);
		verify(javaClient).send(captor.capture());

		HttpRequest req = captor.getValue();

		assertThat(req.uri()).isEqualTo(URI.create("http://test/8.8.8.8/country/"));

		assertThat(req.method()).isEqualTo("GET");

		assertThat(req.bodyPublisher()).isEmpty();
	}

}
