package com.github.randdd32.donor_search_backend.web.dto.filter;

import java.util.List;

public record PowerSupplyFilter(
        String search,
        List<Long> manufacturerIds,
        List<Long> typeIds,
        List<Long> efficiencyIds,
        List<Long> modularIds,
        List<Long> colorIds,
        Integer minWattage,
        Integer maxWattage,
        Integer minLength,
        Integer maxLength
) {}
