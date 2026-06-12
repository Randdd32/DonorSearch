package com.github.randdd32.donor_search_backend.web.dto.filter;

import java.util.List;

public record CpuFilter(
        String search,
        List<Long> manufacturerIds,
        List<Long> socketIds,
        List<Long> microarchitectureIds,
        List<Long> graphicsIds,
        Integer minCoreCount,
        Integer maxCoreCount,
        Double minCoreClock,
        Double maxCoreClock,
        Double minBoostClock,
        Double maxBoostClock,
        Integer minTdp,
        Integer maxTdp,
        Boolean eccSupport
) {}
