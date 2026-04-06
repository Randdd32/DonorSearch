package com.github.randdd32.donor_search_backend.web.dto.filter;

import com.github.randdd32.donor_search_backend.model.enums.ComponentType;
import com.github.randdd32.donor_search_backend.model.enums.MappingConfidence;

import java.time.Instant;

public record IntegrationMappingFilter(
        String search,
        MappingConfidence confidence,
        ComponentType componentType,
        Instant createdAfter,
        Instant createdBefore,
        Instant updatedAfter,
        Instant updatedBefore
) {}
