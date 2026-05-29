package pl.arekbednarz.coupons.domain.exception;

public class GeoLocationUnavailableException extends RuntimeException {
	public GeoLocationUnavailableException(String ip) {
		super("Cannot resolve country for IP %s".formatted(ip));
	}
}
