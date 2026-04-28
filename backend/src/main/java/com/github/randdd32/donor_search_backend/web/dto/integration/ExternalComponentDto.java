package com.github.randdd32.donor_search_backend.web.dto.integration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.randdd32.donor_search_backend.web.dto.integration.enums.ExternalComponentCategory;

public record ExternalComponentDto(
        String adapterId,
        String categoryId,
        String externalName,
        ExternalComponentCategory category,
        @JsonIgnore Long manufacturerId,
        String manufacturerName,
        String serialNumber,
        String note,
        String modelParameters,
        String modelNote,
        String modelProductNumber,
        Long mappedComponentId
) {}
