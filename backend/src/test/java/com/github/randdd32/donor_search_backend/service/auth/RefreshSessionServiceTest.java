package com.github.randdd32.donor_search_backend.service.auth;

import com.github.randdd32.donor_search_backend.model.auth.RefreshSessionEntity;
import com.github.randdd32.donor_search_backend.model.auth.UserEntity;
import com.github.randdd32.donor_search_backend.repository.auth.RefreshSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshSessionServiceTest {
    @Mock
    private RefreshSessionRepository repository;

    @InjectMocks
    private RefreshSessionService sessionService;

    private UserEntity user;
    private RefreshSessionEntity validSession;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(sessionService, "refreshExpirationDays", 60L);

        user = new UserEntity();
        user.setId(1L);

        validSession = new RefreshSessionEntity();
        validSession.setId(10L);
        validSession.setUser(user);
        validSession.setRefreshToken("token-123");
        validSession.setFingerprint("fingerprint-A");
        validSession.setExpiresIn(Instant.now().plus(1, ChronoUnit.DAYS));
    }

    @Test
    @DisplayName("Позитивный тест: создание новой сессии с удалением лишних (лимит 5)")
    void createSession_Positive_EnforcesMaxSessions() {
        List<RefreshSessionEntity> existingSessions = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            RefreshSessionEntity s = new RefreshSessionEntity();
            s.setId((long) i);
            existingSessions.add(s);
        }

        when(repository.findAllByUserIdOrderByCreatedAtAsc(1L)).thenReturn(existingSessions);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RefreshSessionEntity newSession = sessionService.createSession(user, "finger-new", "127.0.0.1", "Chrome");

        assertNotNull(newSession.getRefreshToken());
        assertEquals("finger-new", newSession.getFingerprint());

        verify(repository, times(1)).deleteByUserIdAndFingerprint(1L, "finger-new");
        verify(repository, times(1)).delete(existingSessions.get(0));
    }

    @Test
    @DisplayName("Позитивный тест: успешная ротация сессии")
    void rotateSession_Positive() {
        when(repository.findByRefreshToken("token-123")).thenReturn(Optional.of(validSession));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RefreshSessionEntity rotated = sessionService.rotateSession("token-123", "fingerprint-A", "192.168.1.1", "Firefox");

        verify(repository, times(1)).delete(validSession);
        assertNotEquals("token-123", rotated.getRefreshToken());
        assertEquals("fingerprint-A", rotated.getFingerprint());
    }

    @Test
    @DisplayName("Негативный тест: ротация с неверным отпечатком браузера (Session Theft)")
    void rotateSession_Negative_FingerprintMismatch() {
        when(repository.findByRefreshToken("token-123")).thenReturn(Optional.of(validSession));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                sessionService.rotateSession("token-123", "fingerprint-HACKER", "ip", "agent")
        );
        assertEquals("Invalid session (fingerprint mismatch)", ex.getMessage());
    }

    @Test
    @DisplayName("Негативный тест: ротация истекшей сессии")
    void rotateSession_Negative_SessionExpired() {
        validSession.setExpiresIn(Instant.now().minus(1, ChronoUnit.DAYS));
        when(repository.findByRefreshToken("token-123")).thenReturn(Optional.of(validSession));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                sessionService.rotateSession("token-123", "fingerprint-A", "ip", "agent")
        );
        assertEquals("Session has expired", ex.getMessage());
    }

    @Test
    @DisplayName("Позитивный тест: отзыв конкретной сессии по токену")
    void revokeSession_Positive_ValidToken() {
        when(repository.findByRefreshToken("token-123")).thenReturn(Optional.of(validSession));
        sessionService.revokeSession("token-123");
        verify(repository, times(1)).delete(validSession);
    }

    @Test
    @DisplayName("Позитивный тест: отзыв сессии игнорируется при пустом токене")
    void revokeSession_Positive_NullOrBlankToken() {
        sessionService.revokeSession(null);
        sessionService.revokeSession("");
        sessionService.revokeSession("   ");
        verify(repository, never()).findByRefreshToken(anyString());
        verify(repository, never()).delete(any());
    }

    @Test
    @DisplayName("Позитивный тест: отзыв всех сессий пользователя")
    void revokeAllUserSessions_Positive() {
        sessionService.revokeAllUserSessions(1L);
        verify(repository, times(1)).deleteAllByUserId(1L);
    }
}
