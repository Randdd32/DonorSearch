package com.github.randdd32.donor_search_backend.web.dto.filter;

import java.util.List;

public record CaseFanFilter(
        String search,
        List<Long> manufacturerIds,
        List<Long> colorIds,
        List<Long> connectorIds,
        List<Integer> sizes,
        Boolean pwm,
        Integer minRpm,
        Integer maxRpm,
        Integer minAirflow,
        Integer maxAirflow
) {}
