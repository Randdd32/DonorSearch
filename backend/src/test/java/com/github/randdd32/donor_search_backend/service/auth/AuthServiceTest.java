package com.github.randdd32.donor_search_backend.service.auth;

import com.github.randdd32.donor_search_backend.core.configuration.security.JwtProvider;
import com.github.randdd32.donor_search_backend.model.auth.RefreshSessionEntity;
import com.github.randdd32.donor_search_backend.model.auth.UserEntity;
import com.github.randdd32.donor_search_backend.model.enums.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

    @Test
    @DisplayName("Позитивный тест: успешный вход в систему (Login)")
    void login_Positive() {
        UserEntity user = new UserEntity();
        user.setUsername("testuser");
        user.setRole(UserRole.USER);

        RefreshSessionEntity session = new RefreshSessionEntity();
        session.setRefreshToken("refresh-token-uuid");

        when(userService.validateAndGetUser("testuser", "password")).thenReturn(user);
        when(sessionService.createSession(user, "fingerprint", "ip", "agent")).thenReturn(session);
        when(jwtProvider.generateAccessToken(user)).thenReturn("access-token-jwt");

        AuthService.AuthResult result = authService.login("testuser", "password", "fingerprint", "ip", "agent");

        assertNotNull(result);
        assertEquals("access-token-jwt", result.responseDto().accessToken());
        assertEquals("refresh-token-uuid", result.refreshToken());
        verify(userService, times(1)).validateAndGetUser(anyString(), anyString());
    }

    @Test
    @DisplayName("Позитивный тест: выход из системы (Logout)")
    void logout_Positive() {
        authService.logout("refresh-token-uuid");
        verify(sessionService, times(1)).revokeSession("refresh-token-uuid");
    }
}
