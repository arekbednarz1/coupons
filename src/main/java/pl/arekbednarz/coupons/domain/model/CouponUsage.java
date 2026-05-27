package pl.arekbednarz.coupons.domain.model;

import pl.arekbednarz.coupons.domain.model.value.CountryCode;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class CouponUsage {

    private final UUID id;
    private final UUID couponId;
    private final String userId;
    private final String ipAddress;
    private final CountryCode countryCode;
    private final Instant usedAt;

    private CouponUsage(UUID id,
                        UUID couponId,
                        String userId,
                        String ipAddress,
                        CountryCode countryCode,
                        Instant usedAt) {

        this.id = Objects.requireNonNull(id);
        this.couponId = Objects.requireNonNull(couponId);
        this.userId = Objects.requireNonNull(userId);
        this.ipAddress = Objects.requireNonNull(ipAddress);
        this.countryCode = Objects.requireNonNull(countryCode);
        this.usedAt = Objects.requireNonNull(usedAt);
    }

    public static CouponUsage createNew(UUID couponId,
                                        String userId,
                                        String ipAddress,
                                        String rawCountry) {
        return new CouponUsage(
                UUID.randomUUID(),
                couponId,
                userId,
                ipAddress,
                CountryCode.of(rawCountry),
                Instant.now()
        );
    }

    public UUID id() { return id; }
    public UUID couponId() { return couponId; }
    public String userId() { return userId; }
    public String ipAddress() { return ipAddress; }
    public CountryCode countryCode() { return countryCode; }
    public Instant usedAt() { return usedAt; }
}
