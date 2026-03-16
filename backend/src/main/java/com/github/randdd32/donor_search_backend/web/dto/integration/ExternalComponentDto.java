package com.github.randdd32.donor_search_backend.web.dto.integration;

import com.github.randdd32.donor_search_backend.web.dto.integration.enums.ExternalComponentCategory;

public record ExternalComponentDto(
        Long adapterId,
        Long categoryId,
        String externalName,
        ExternalComponentCategory category,
        String manufacturerName,
        String serialNumber,
        Long mappedComponentId
) {}
