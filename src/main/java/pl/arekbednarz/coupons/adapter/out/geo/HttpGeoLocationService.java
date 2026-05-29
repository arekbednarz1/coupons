package pl.arekbednarz.coupons.adapter.out.geo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.arekbednarz.coupons.domain.port.GeoLocationService;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;


@Component
public class HttpGeoLocationService implements GeoLocationService {

	private final JavaHttpClientFacade client;

	private final String geoApi;

	public HttpGeoLocationService(
		@Value("${geo.api.url}") String geoApi,
		JavaHttpClientFacade client) {
		this.client = client;
		this.geoApi = geoApi;
	}

	@Override
	public Optional<String> resolveCountryCode(String ipAddress) {
		try {
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(geoApi + "/" + ipAddress + "/country/"))
				.GET()
				.build();

			HttpResponse<String> response = client.send(request);

			if (response.statusCode() != 200) {
				return Optional.empty();
			}

			String country = response.body().trim().toUpperCase();
			return country.isBlank() ? Optional.empty() : Optional.of(country);

		} catch (Exception ex) {
			return Optional.empty();
		}
	}
}
