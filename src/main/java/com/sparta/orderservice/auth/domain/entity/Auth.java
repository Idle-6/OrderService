package com.sparta.orderservice.auth.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.cglib.core.internal.LoadingCache;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_auth")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Auth {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String email;
    @Column(name = "token_hash", nullable = false, length = 64)
    private String tokenHash;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
    @Column(name = "consumed_at")
    private LocalDateTime consumedAt; // 재사용 제한

    public boolean isExpired() { return expiresAt.isBefore(LocalDateTime.now()); }
    public boolean isConsumed() { return consumedAt != null; }

    public void updateConsumedAt(LocalDateTime time) {
        this.consumedAt = time;
    }
}
