package com.github.randdd32.donor_search_backend.web.dto.auth;

import com.github.randdd32.donor_search_backend.model.enums.UserRole;

import java.time.Instant;

public record UserDto(
        Long id,
        String username,
        UserRole role,
        Instant createdAt,
        Instant updatedAt
) {}
