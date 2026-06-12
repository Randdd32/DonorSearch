package com.github.randdd32.donor_search_backend.web.dto.filter;

import java.util.List;

public record CpuCoolerFilter(
        String search,
        List<Long> manufacturerIds,
        List<Long> colorIds,
        List<Long> socketIds,
        Boolean isWaterCooled,
        Integer minHeight,
        Integer maxHeight,
        Integer minWaterSize,
        Integer maxWaterSize,
        Integer minRpm,
        Integer maxRpm
) {}
