package pl.arekbednarz.coupons.domain.model.value;

import java.util.Locale;
import java.util.Objects;

public final class CouponCode {

    private final String value;

    private CouponCode(String value) {
        this.value = value;
    }

    public static CouponCode of(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Coupon code must not be blank");
        }
        return new CouponCode(raw.trim().toUpperCase(Locale.ROOT));
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof CouponCode other) && value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
