package pl.arekbednarz.coupons.domain.model.value;

import java.util.Locale;
import java.util.Objects;

public final class CountryCode {

    private final String value;

    private CountryCode(String value) {
        this.value = value;
    }

    public static CountryCode of(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Country code must not be blank");
        }
        if (raw.length() != 2) {
            throw new IllegalArgumentException("Country code must be 2 letters");
        }
        return new CountryCode(raw.trim().toUpperCase(Locale.ROOT));
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof CountryCode other) && value.equals(other.value);
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

