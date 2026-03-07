package com.github.randdd32.donor_search_backend.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.randdd32.donor_search_backend.model.enums.ComponentType;
import com.github.randdd32.donor_search_backend.model.enums.MappingConfidence;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record IntegrationMappingDto(
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        Long id,

        @NotBlank
        String externalName,

        @NotNull
        Long internalComponentId,

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        String internalComponentName,

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        ComponentType internalComponentType,

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        String internalComponentSearchName,

        @NotNull
        MappingConfidence confidence,

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        Instant createdAt,

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        Instant updatedAt
) {}
