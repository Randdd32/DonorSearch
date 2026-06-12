package com.github.randdd32.donor_search_backend.web.dto.auth;

import com.github.randdd32.donor_search_backend.core.configuration.Constants;
import com.github.randdd32.donor_search_backend.model.enums.UserRole;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UserUpdateDto(
        @NotNull
        UserRole role,

        @Pattern(
                regexp = Constants.PASSWORD_PATTERN,
                message = "Password must contain 8-60 characters, at least one uppercase letter, one lowercase letter, one digit, and one special character (!@#$%^&*_=+-)."
        )
        String password
) {}
