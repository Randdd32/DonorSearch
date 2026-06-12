package com.github.randdd32.donor_search_backend.web.dto.auth;

import com.github.randdd32.donor_search_backend.core.configuration.Constants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequestDto(
        @NotBlank
        @Size(min = 2, max = 100)
        String username,

        @NotBlank
        @Pattern(
                regexp = Constants.PASSWORD_PATTERN,
                message = "Password must contain 8-60 characters, at least one uppercase letter, one lowercase letter, one digit, and one special character (!@#$%^&*_=+-)."
        )
        String password,

        @NotBlank
        @Size(max = 200)
        String fingerprint
) {}
