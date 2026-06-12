package com.github.randdd32.donor_search_backend.web.dto.hardware;

import java.util.List;

public record StorageDto(
        Long id,
        String name,
        String manufacturerName,
        String typeName,
        String formFactorName,
        String colorName,

        Boolean isExternal,
        Integer capacityGb,
        Integer cacheMb,
        Integer rpm,

        List<String> interfaces,
        List<String> partNumbers
) {}
