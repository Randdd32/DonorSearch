package com.github.randdd32.donor_search_backend.model.auth;

import com.github.randdd32.donor_search_backend.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "refresh_session")
@EntityListeners(AuditingEntityListener.class)
public class RefreshSessionEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "refresh_token", nullable = false, unique = true, length = 36)
    private String refreshToken;

    @Column(nullable = false, length = 200)
    private String fingerprint;

    @Column(nullable = false, length = 50)
    private String ip;

    @Column(name = "user_agent", nullable = false, length = 500)
    private String userAgent;

    @Column(name = "expires_in", nullable = false)
    private Instant expiresIn;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
