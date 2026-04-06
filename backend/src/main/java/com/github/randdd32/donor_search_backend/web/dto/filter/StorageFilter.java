package com.github.randdd32.donor_search_backend.web.dto.filter;

import java.util.List;

public record StorageFilter(
        String search,
        List<Long> manufacturerIds,
        List<Long> typeIds,
        List<Long> formFactorIds,
        List<Long> colorIds,
        List<Long> interfaceIds,
        Boolean isExternal,
        Integer minCapacity,
        Integer maxCapacity,
        Integer minCache,
        Integer maxCache,
        Integer minRpm,
        Integer maxRpm
) {}
