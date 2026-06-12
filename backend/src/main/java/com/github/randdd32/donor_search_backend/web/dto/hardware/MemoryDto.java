package com.github.randdd32.donor_search_backend.web.dto.hardware;

import java.util.List;

public record MemoryDto(
        Long id,
        String name,
        String manufacturerName,
        String formFactorName,
        String memoryTypeName,
        String colorName,

        Integer casLatency,
        Integer frequencyMhz,
        Integer modulesCount,
        Integer modulesSizeGb,

        Boolean isEcc,
        Boolean isRegistered,

        List<String> partNumbers
) {}
