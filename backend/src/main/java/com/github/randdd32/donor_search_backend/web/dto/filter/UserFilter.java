package com.github.randdd32.donor_search_backend.web.dto.filter;

import com.github.randdd32.donor_search_backend.model.enums.UserRole;

import java.time.Instant;
import java.util.List;

public record UserFilter(
        String search,
        List<UserRole> roles,
        Instant createdAfter,
        Instant createdBefore,
        Instant updatedAfter,
        Instant updatedBefore
) {}
