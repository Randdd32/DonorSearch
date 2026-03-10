package com.github.randdd32.donor_search_backend.web.dto.hardware;

import java.util.List;

public record CpuDto(
        Long id,
        String name,
        String manufacturerName,
        String socketName,
        String microarchitectureName,
        String graphicsName,

        Integer coreCount,
        Double coreClockGhz,
        Double boostClockGhz,
        Integer tdpW,
        Integer maxMemoryGb,
        Boolean eccSupport,

        List<String> partNumbers
) {}
