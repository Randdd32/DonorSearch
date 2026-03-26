package com.github.randdd32.donor_search_backend.web.dto.search;

import com.github.randdd32.donor_search_backend.web.dto.integration.ExternalComponentDto;

import java.util.List;

public record CompatibleComponentDto(
        ExternalComponentDto externalInfo,
        int componentPenalty,
        List<DonorWarningDto> componentWarnings
) {}
