package com.github.randdd32.donor_search_backend.web.dto.filter;

import java.util.List;

public record MotherboardFilter(
        String search,
        List<Long> manufacturerIds,
        List<Long> socketIds,
        List<Long> formFactorIds,
        List<Long> memoryTypeIds,
        Integer minMaxMemoryGb,
        Integer minMemorySlots,
        Integer minMemorySpeedMhz,
        Boolean eccSupport,
        Boolean usesBackConnect
) {}
