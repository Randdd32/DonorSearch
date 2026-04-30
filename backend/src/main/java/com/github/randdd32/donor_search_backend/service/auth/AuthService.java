package com.github.randdd32.donor_search_backend.service.auth;

import com.github.randdd32.donor_search_backend.core.configuration.security.JwtProvider;
import com.github.randdd32.donor_search_backend.model.auth.RefreshSessionEntity;
import com.github.randdd32.donor_search_backend.model.auth.UserEntity;
import com.github.randdd32.donor_search_backend.web.dto.auth.AuthResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final RefreshSessionService sessionService;
    private final JwtProvider jwtProvider;

    @Transactional
    public AuthResult login(String username, String password, String fingerprint, String ip, String userAgent) {
        UserEntity user = userService.validateAndGetUser(username, password);
        RefreshSessionEntity session = sessionService.createSession(user, fingerprint, ip, userAgent);
        String accessToken = jwtProvider.generateAccessToken(user);

        return new AuthResult(new AuthResponseDto(accessToken, user.getUsername(), user.getRole()), session.getRefreshToken());
    }

    @Transactional
    public AuthResult refresh(String oldRefreshToken, String fingerprint, String ip, String userAgent) {
        RefreshSessionEntity newSession = sessionService.rotateSession(oldRefreshToken, fingerprint, ip, userAgent);
        UserEntity user = newSession.getUser();
        String accessToken = jwtProvider.generateAccessToken(user);

        return new AuthResult(new AuthResponseDto(accessToken, user.getUsername(), user.getRole()), newSession.getRefreshToken());
    }

    @Transactional
    public void logout(String refreshToken) {
        sessionService.revokeSession(refreshToken);
    }

    public record AuthResult(AuthResponseDto responseDto, String refreshToken) {}
}
