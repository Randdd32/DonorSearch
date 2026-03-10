package com.github.randdd32.donor_search_backend.web.dto.hardware;

import java.util.List;

public record CaseFanDto(
        Long id,
        String name,
        String manufacturerName,
        String colorName,

        Integer sizeMm,
        Boolean pwm,
        Integer rpmMin,
        Integer rpmMax,
        Integer airflowMin,
        Integer airflowMax,

        List<String> connectors,
        List<String> partNumbers
) {}
