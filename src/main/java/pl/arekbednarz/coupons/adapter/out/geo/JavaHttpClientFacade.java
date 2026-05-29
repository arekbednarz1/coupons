package pl.arekbednarz.coupons.adapter.out.geo;

import org.springframework.stereotype.Component;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


@Component
public class JavaHttpClientFacade implements HttpClientFacade {

	private final HttpClient client = HttpClient.newHttpClient();

	@Override
	public HttpResponse<String> send(HttpRequest request) throws Exception {
		return client.send(request, HttpResponse.BodyHandlers.ofString());
	}
}
