package com.github.randdd32.donor_search_backend.repository.auth;

import com.github.randdd32.donor_search_backend.model.auth.RefreshSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RefreshSessionRepository extends JpaRepository<RefreshSessionEntity, Long> {
    Optional<RefreshSessionEntity> findByRefreshToken(String refreshToken);

    List<RefreshSessionEntity> findAllByUserIdOrderByCreatedAtAsc(Long userId);

    void deleteByUserIdAndFingerprint(Long userId, String fingerprint);

    void deleteAllByUserId(Long userId);

    @Modifying
    @Query(value = """
        DELETE FROM refresh_session
        WHERE user_id = :userId
        AND id NOT IN (
            SELECT id FROM (
                SELECT id FROM refresh_session
                WHERE user_id = :userId
                ORDER BY created_at DESC
                LIMIT :keepCount
            ) AS recent
        )
    """, nativeQuery = true)
    void deleteExcessSessions(@Param("userId") Long userId,
                              @Param("keepCount") int keepCount);
}
