package pl.arekbednarz.coupons.domain.port;

import java.util.Optional;


public interface GeoLocationService {

	Optional<String> resolveCountryCode(String ipAddress);
}
