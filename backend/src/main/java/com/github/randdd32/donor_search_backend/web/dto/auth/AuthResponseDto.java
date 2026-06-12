package com.github.randdd32.donor_search_backend.web.dto.auth;

import com.github.randdd32.donor_search_backend.model.enums.UserRole;

public record AuthResponseDto(
        String accessToken,
        String username,
        UserRole role
) {}
