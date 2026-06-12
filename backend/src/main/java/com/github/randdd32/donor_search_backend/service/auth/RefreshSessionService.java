package com.github.randdd32.donor_search_backend.service.auth;

import com.github.randdd32.donor_search_backend.model.auth.RefreshSessionEntity;
import com.github.randdd32.donor_search_backend.model.auth.UserEntity;
import com.github.randdd32.donor_search_backend.repository.auth.RefreshSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshSessionService {
    private final RefreshSessionRepository repository;

    @Value("${jwt.refresh-expiration-days:60}")
    private long refreshExpirationDays;

    private static final int MAX_SESSIONS = 5;

    @Transactional
    public RefreshSessionEntity createSession(UserEntity user, String fingerprint, String ip, String userAgent) {
        repository.deleteByUserIdAndFingerprint(user.getId(), fingerprint);

        checkAndCleanSessions(user.getId());

        RefreshSessionEntity session = new RefreshSessionEntity();
        session.setUser(user);
        session.setRefreshToken(UUID.randomUUID().toString());
        session.setFingerprint(fingerprint);
        session.setIp(ip != null ? ip : "unknown");
        session.setUserAgent(userAgent != null ? (userAgent.length() > 500 ? userAgent.substring(0, 500) : userAgent) : "unknown");
        session.setExpiresIn(Instant.now().plus(refreshExpirationDays, ChronoUnit.DAYS));

        return repository.save(session);
    }

    @Transactional
    public RefreshSessionEntity rotateSession(String oldToken, String fingerprint, String ip, String userAgent) {
        if (oldToken == null || oldToken.isBlank()) {
            throw new IllegalArgumentException("Missing refresh token in cookie");
        }

        RefreshSessionEntity session = repository.findByRefreshToken(oldToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid session (token not found)"));

        UserEntity user = session.getUser();
        repository.delete(session);

        if (Instant.now().isAfter(session.getExpiresIn())) {
            throw new IllegalArgumentException("Session has expired");
        }

        if (!session.getFingerprint().equals(fingerprint)) {
            log.warn("Session theft attempt! Expected fingerprint: {}, actual: {}", session.getFingerprint(), fingerprint);
            throw new IllegalArgumentException("Invalid session (fingerprint mismatch)");
        }

        return createSession(user, fingerprint, ip, userAgent);
    }

    @Transactional
    public void revokeSession(String token) {
        if (token == null || token.isBlank()) {
            return;
        }
        repository.findByRefreshToken(token).ifPresent(repository::delete);
    }

    @Transactional
    public void revokeAllUserSessions(Long userId) {
        repository.deleteAllByUserId(userId);
    }

    private void checkAndCleanSessions(Long userId) {
        repository.deleteExcessSessions(userId, MAX_SESSIONS - 1);
    }
}
