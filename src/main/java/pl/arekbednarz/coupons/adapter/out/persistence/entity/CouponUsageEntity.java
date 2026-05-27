package pl.arekbednarz.coupons.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "coupon_usage", uniqueConstraints = {
        @UniqueConstraint(name = "uk_coupon_user", columnNames = {"coupon_id", "user_id"})
})
public class CouponUsageEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "coupon_id", nullable = false)
    private UUID couponId;

    @Column(name = "user_id", nullable = false, length = 128)
    private String userId;

    @Column(name = "ip_address", nullable = false, length = 64)
    private String ipAddress;

    @Column(name = "country_code", nullable = false, length = 2)
    private String countryCode;

    @Column(name = "used_at", nullable = false)
    private Instant usedAt;
}
