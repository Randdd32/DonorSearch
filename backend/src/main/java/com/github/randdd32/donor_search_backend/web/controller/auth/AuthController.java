package com.github.randdd32.donor_search_backend.web.controller.auth;

import com.github.randdd32.donor_search_backend.core.configuration.Constants;
import com.github.randdd32.donor_search_backend.service.auth.AuthService;
import com.github.randdd32.donor_search_backend.web.dto.auth.AuthResponseDto;
import com.github.randdd32.donor_search_backend.web.dto.auth.LoginRequestDto;
import com.github.randdd32.donor_search_backend.web.dto.auth.RefreshRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.API_URL + "/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Value("${jwt.refresh-expiration-days:60}")
    private long refreshExpirationDays;

    private static final String REFRESH_COOKIE_NAME = "refreshToken";
    private static final String AUTH_PATH = Constants.API_URL + "/auth";

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto request, HttpServletRequest httpRequest) {
        String ip = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader(HttpHeaders.USER_AGENT);

        AuthService.AuthResult result = authService.login(request.username(), request.password(), request.fingerprint(),
                ip, userAgent);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, buildRefreshCookie(result.refreshToken(), refreshExpirationDays * 24 * 60 * 60))
                .body(result.responseDto());
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refresh(
            @CookieValue(value = REFRESH_COOKIE_NAME, required = false) String refreshToken,
            @Valid @RequestBody RefreshRequestDto request,
            HttpServletRequest httpRequest) {
        String ip = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader(HttpHeaders.USER_AGENT);

        AuthService.AuthResult result = authService.refresh(refreshToken, request.fingerprint(), ip, userAgent);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, buildRefreshCookie(result.refreshToken(), refreshExpirationDays * 24 * 60 * 60))
                .body(result.responseDto());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(value = REFRESH_COOKIE_NAME, required = false) String refreshToken) {
        authService.logout(refreshToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, buildRefreshCookie("", 0))
                .build();
    }

    private String buildRefreshCookie(String token, long maxAgeSeconds) {
        return ResponseCookie.from(REFRESH_COOKIE_NAME, token)
                .httpOnly(true)
                .secure(false)
                .path(AUTH_PATH)
                .maxAge(maxAgeSeconds)
                .sameSite("Strict")
                .build()
                .toString();
    }
}
