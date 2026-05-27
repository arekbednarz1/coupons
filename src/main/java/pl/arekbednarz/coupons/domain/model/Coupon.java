package pl.arekbednarz.coupons.domain.model;

import pl.arekbednarz.coupons.domain.exception.CouponExhaustedException;
import pl.arekbednarz.coupons.domain.exception.CouponForbiddenCountryException;
import pl.arekbednarz.coupons.domain.model.value.CountryCode;
import pl.arekbednarz.coupons.domain.model.value.CouponCode;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class Coupon {

    private final UUID id;
    private final CouponCode code;
    private final Instant createdAt;
    private final int maxUsages;
    private final int currentUsages;
    private final CountryCode countryCode;
    private final long version;

    private Coupon(UUID id,
                   CouponCode code,
                   Instant createdAt,
                   int maxUsages,
                   int currentUsages,
                   CountryCode countryCode,
                   long version) {

        this.id = Objects.requireNonNull(id);
        this.code = Objects.requireNonNull(code);
        this.createdAt = Objects.requireNonNull(createdAt);

        if (maxUsages <= 0) {
            throw new IllegalArgumentException("maxUsages must be > 0");
        }
        if (currentUsages < 0 || currentUsages > maxUsages) {
            throw new IllegalArgumentException("Invalid currentUsages");
        }

        this.maxUsages = maxUsages;
        this.currentUsages = currentUsages;
        this.countryCode = Objects.requireNonNull(countryCode);
        this.version = version;
    }

    public static Coupon createNew(String rawCode, int maxUsages, String rawCountry) {
        return new Coupon(
                UUID.randomUUID(),
                CouponCode.of(rawCode),
                Instant.now(),
                maxUsages,
                0,
                CountryCode.of(rawCountry),
                0L
        );
    }

    public Coupon useOnce() {
        if (currentUsages >= maxUsages) {
            throw new CouponExhaustedException(code.value());
        }
        return new Coupon(
                id,
                code,
                createdAt,
                maxUsages,
                currentUsages + 1,
                countryCode,
                version + 1
        );
    }

    public void validateCountry(CountryCode userCountry) {
        if (!this.countryCode.equals(userCountry)) {
            throw new CouponForbiddenCountryException(code.value(), userCountry.value());
        }
    }

    public UUID id() { return id; }
    public CouponCode code() { return code; }
    public Instant createdAt() { return createdAt; }
    public int maxUsages() { return maxUsages; }
    public int currentUsages() { return currentUsages; }
    public CountryCode countryCode() { return countryCode; }
    public long version() { return version; }
}
