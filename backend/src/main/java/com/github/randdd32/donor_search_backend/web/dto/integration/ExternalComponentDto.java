package com.github.randdd32.donor_search_backend.web.dto.integration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.randdd32.donor_search_backend.web.dto.integration.enums.ExternalComponentCategory;

public record ExternalComponentDto(
        Long adapterId,
        Long categoryId,
        String externalName,
        ExternalComponentCategory category,
        @JsonIgnore Long manufacturerId,
        String manufacturerName,
        String serialNumber,
        Long mappedComponentId
) {}
