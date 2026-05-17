package com.github.randdd32.donor_search_backend.service.auth;

import com.github.randdd32.donor_search_backend.core.configuration.security.JwtProvider;
import com.github.randdd32.donor_search_backend.model.auth.RefreshSessionEntity;
import com.github.randdd32.donor_search_backend.model.auth.UserEntity;
import com.github.randdd32.donor_search_backend.model.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserService userService;
    @Mock
    private RefreshSessionService sessionService;
    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private AuthService authService;

    private UserEntity user;
    private RefreshSessionEntity session;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setUsername("testuser");
        user.setRole(UserRole.USER);

        session = new RefreshSessionEntity();
        session.setRefreshToken("refresh-token-uuid");
        session.setUser(user);
    }

    @Test
    @DisplayName("Позитивный тест: успешный вход в систему (Login)")
    void login_Positive() {
        when(userService.validateAndGetUser("testuser", "password")).thenReturn(user);
        when(sessionService.createSession(user, "fingerprint", "ip", "agent")).thenReturn(session);
        when(jwtProvider.generateAccessToken(user)).thenReturn("access-token-jwt");

        AuthService.AuthResult result = authService.login("testuser", "password", "fingerprint", "ip", "agent");

        assertNotNull(result);
        assertEquals("access-token-jwt", result.responseDto().accessToken());
        assertEquals("refresh-token-uuid", result.refreshToken());
    }

    @Test
    @DisplayName("Негативный тест: неверный логин или пароль")
    void login_Negative_InvalidCredentials() {
        when(userService.validateAndGetUser("testuser", "wrong-pass"))
                .thenThrow(new IllegalArgumentException("Invalid username or password"));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                authService.login("testuser", "wrong-pass", "fingerprint", "ip", "agent")
        );

        assertEquals("Invalid username or password", ex.getMessage());
        verify(sessionService, never()).createSession(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Позитивный тест: успешное обновление токена (Refresh)")
    void refresh_Positive() {
        RefreshSessionEntity newSession = new RefreshSessionEntity();
        newSession.setRefreshToken("new-refresh-token");
        newSession.setUser(user);

        when(sessionService.rotateSession("old-token", "fingerprint", "ip", "agent")).thenReturn(newSession);
        when(jwtProvider.generateAccessToken(user)).thenReturn("new-access-token");

        AuthService.AuthResult result = authService.refresh("old-token", "fingerprint", "ip", "agent");

        assertNotNull(result);
        assertEquals("new-access-token", result.responseDto().accessToken());
        assertEquals("new-refresh-token", result.refreshToken());
    }

    @Test
    @DisplayName("Негативный тест: ошибка обновления токена (попытка кражи сессии)")
    void refresh_Negative_SessionTheft() {
        when(sessionService.rotateSession("old-token", "hacker-fingerprint", "ip", "agent"))
                .thenThrow(new IllegalArgumentException("Invalid session (fingerprint mismatch)"));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                authService.refresh("old-token", "hacker-fingerprint", "ip", "agent")
        );

        assertEquals("Invalid session (fingerprint mismatch)", ex.getMessage());
        verify(jwtProvider, never()).generateAccessToken(any());
    }

    @Test
    @DisplayName("Позитивный тест: выход из системы (Logout)")
    void logout_Positive() {
        authService.logout("refresh-token-uuid");
        verify(sessionService, times(1)).revokeSession("refresh-token-uuid");
    }
}
