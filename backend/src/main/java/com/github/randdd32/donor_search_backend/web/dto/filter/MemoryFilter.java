package com.github.randdd32.donor_search_backend.web.dto.filter;

import java.util.List;

public record MemoryFilter(
        String search,
        List<Long> manufacturerIds,
        List<Long> formFactorIds,
        List<Long> memoryTypeIds,
        List<Long> colorIds,
        Integer minFrequency,
        Integer maxFrequency,
        Integer minModulesCount,
        Integer maxModulesCount,
        Integer minModulesSize,
        Integer maxModulesSize,
        Integer minCas,
        Integer maxCas,
        Boolean isEcc,
        Boolean isRegistered
) {}
