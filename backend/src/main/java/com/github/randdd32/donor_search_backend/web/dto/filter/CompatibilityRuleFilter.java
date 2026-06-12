package com.github.randdd32.donor_search_backend.web.dto.filter;

import com.github.randdd32.donor_search_backend.model.enums.ComponentType;

import java.time.Instant;
import java.util.List;

public record CompatibilityRuleFilter(
        String search,
        Boolean isActive,
        List<ComponentType> targetTypes,
        Instant createdAfter,
        Instant createdBefore,
        Instant updatedAfter,
        Instant updatedBefore
) {}
