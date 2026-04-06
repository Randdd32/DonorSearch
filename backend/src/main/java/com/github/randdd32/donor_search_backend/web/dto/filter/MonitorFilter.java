package com.github.randdd32.donor_search_backend.web.dto.filter;

import java.util.List;

public record MonitorFilter(
        String search,
        List<Long> manufacturerIds,
        List<Long> resolutionIds,
        List<Long> panelTypeIds,
        List<Long> aspectRatioIds,
        Double minScreenSize,
        Double maxScreenSize,
        Integer minRefreshRate,
        Integer maxRefreshRate,
        Double minResponseTime,
        Double maxResponseTime
) {}
