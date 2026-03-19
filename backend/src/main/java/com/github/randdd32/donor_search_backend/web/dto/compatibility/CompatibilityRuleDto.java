package com.github.randdd32.donor_search_backend.web.dto.compatibility;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.randdd32.donor_search_backend.model.enums.ComponentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.Set;

public record CompatibilityRuleDto(
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        Long id,

        @NotBlank
        @Size(min = 1, max = 100)
        String ruleCode,

        @NotBlank
        String expression,

        @NotBlank
        String errorMessage,

        @NotNull
        Boolean isActive,

        @NotEmpty
        Set<ComponentType> targetComponentTypes,

        String description,

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        Instant createdAt,

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        Instant updatedAt
) {}
