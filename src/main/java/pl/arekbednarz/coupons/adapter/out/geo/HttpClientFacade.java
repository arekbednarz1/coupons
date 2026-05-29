package pl.arekbednarz.coupons.adapter.out.geo;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public interface HttpClientFacade {
	HttpResponse<String> send(HttpRequest request) throws Exception;
}
