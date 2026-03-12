package com.github.randdd32.donor_search_backend.web.dto.integration;

public record ExternalComponentDto(
        Long adapterId,
        Long pcId,
        String externalName,
        String adapterCategory
) {}
