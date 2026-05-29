package pl.arekbednarz.coupons.adapter.out.geo;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.arekbednarz.coupons.domain.port.GeoLocationService;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class HttpGeoLocationService implements GeoLocationService {

	@Value("${geo.api.url}")
	public String geoApi;

	private final HttpClient client = HttpClient.newHttpClient();

	@Override
	public Optional<String> resolveCountryCode(String ipAddress) {
		try {
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(geoApi + ipAddress + "/country/"))
				.GET()
				.build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() != 200) {
				return Optional.empty();
			}

			String country = response.body().trim().toUpperCase();
			if (country.isBlank()) {
				return Optional.empty();
			}

			return Optional.of(country);

		} catch (Exception ex) {
			return Optional.empty();
		}
	}
}
