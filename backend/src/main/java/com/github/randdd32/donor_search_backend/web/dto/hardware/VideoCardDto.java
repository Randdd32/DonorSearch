package com.github.randdd32.donor_search_backend.web.dto.hardware;

import java.util.List;
import java.util.Map;

public record VideoCardDto(
        Long id,
        String name,
        String manufacturerName,
        String chipsetName,
        String memoryTypeName,
        String interfaceName,
        String colorName,

        Integer memoryGb,
        Integer coreClockMhz,
        Integer boostClockMhz,
        Integer lengthMm,
        Integer tdpW,
        Integer slotWidth,
        Integer caseExpansionWidth,

        Integer power6pinCount,
        Integer power8pinCount,
        Integer power12pinCount,
        Integer power12vhpwrCount,
        Integer powerEpsCount,

        Map<String, Integer> videoOutputs,
        List<String> partNumbers
) {}
