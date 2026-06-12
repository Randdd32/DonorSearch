package com.github.randdd32.donor_search_backend.web.dto.hardware;

import java.util.List;

public record CpuCoolerDto(
        Long id,
        String name,
        String manufacturerName,
        String colorName,

        Boolean isWaterCooled,
        Integer heightMm,
        Integer waterCooledSizeMm,
        Integer rpmMin,
        Integer rpmMax,

        List<String> sockets,
        List<String> partNumbers
) {}
