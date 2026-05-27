package pl.arekbednarz.coupons.adapter.in.web.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.arekbednarz.coupons.domain.exception.*;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<Object> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(
                Map.of(
                        "timestamp", Instant.now().toString(),
                        "status", status.value(),
                        "error", status.getReasonPhrase(),
                        "message", message
                )
        );
    }

    @ExceptionHandler(CouponNotFoundException.class)
    public ResponseEntity<Object> handleNotFound(CouponNotFoundException ex) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({
            CouponExhaustedException.class,
            CouponForbiddenCountryException.class,
            CouponAlreadyUsedByUserException.class
    })
    public ResponseEntity<Object> handleBadRequest(RuntimeException ex) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(GeoLocationUnavailableException.class)
    public ResponseEntity<Object> handleGeo(GeoLocationUnavailableException ex) {
        return error(HttpStatus.BAD_GATEWAY, ex.getMessage());
    }

    @ExceptionHandler(CouponConcurrentUsageException.class)
    public ResponseEntity<Object> handleConcurrent(CouponConcurrentUsageException ex) {
        return error(HttpStatus.CONFLICT, ex.getMessage());
    }
}
