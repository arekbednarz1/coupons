package pl.arekbednarz.coupons.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "coupons", uniqueConstraints = {
        @UniqueConstraint(name = "uk_coupon_code", columnNames = "code")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, updatable = false, length = 64)
    private String code;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private int maxUsages;

    @Column(nullable = false)
    private int currentUsages;

    @Column(nullable = false, length = 2)
    private String countryCode;

    @Version
    private long version;
}
