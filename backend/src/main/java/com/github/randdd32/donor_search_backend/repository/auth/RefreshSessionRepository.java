package com.github.randdd32.donor_search_backend.repository.auth;

import com.github.randdd32.donor_search_backend.model.auth.RefreshSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshSessionRepository extends JpaRepository<RefreshSessionEntity, Long> {
    Optional<RefreshSessionEntity> findByRefreshToken(String refreshToken);
    List<RefreshSessionEntity> findAllByUserIdOrderByCreatedAtAsc(Long userId);
    void deleteByUserIdAndFingerprint(Long userId, String fingerprint);
    void deleteAllByUserId(Long userId);
}
