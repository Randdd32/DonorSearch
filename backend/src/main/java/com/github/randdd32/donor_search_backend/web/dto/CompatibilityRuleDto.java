package com.github.randdd32.donor_search_backend.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

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

        String description,

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        Instant createdAt,

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        Instant updatedAt
) {}
