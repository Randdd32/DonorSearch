package com.github.randdd32.donor_search_backend.web.dto.filter;

import java.util.List;

public record VideoCardFilter(
        String search,
        List<Long> manufacturerIds,
        List<Long> chipsetIds,
        List<Long> memoryTypeIds,
        Integer minLength,
        Integer maxLength,
        Integer minTdp,
        Integer maxTdp,
        Integer slotWidth
) {}
