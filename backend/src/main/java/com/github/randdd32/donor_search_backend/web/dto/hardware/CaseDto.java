package com.github.randdd32.donor_search_backend.web.dto.hardware;

import java.util.List;

public record CaseDto(
        Long id,
        String name,
        String manufacturerName,
        String caseTypeName,
        String colorName,
        String sidePanelName,

        Integer lengthMm,
        Integer widthMm,
        Integer heightMm,
        Integer maxGpuLenMm,
        Integer maxCpuCoolerHeightMm,

        Integer int35Bays,
        Integer ext525Bays,
        Integer ext35Bays,
        Integer int25Bays,

        Integer expansionSlotsFullHeight,
        Integer expansionSlotsHalfHeight,
        Integer expansionSlotsRiser,

        String radiatorSupportText,
        String fanSupportText,
        List<Integer> radiatorSizes,
        List<Integer> fanSizes,

        List<String> moboFormFactors,
        List<String> frontPanelUsbTypes,
        List<String> partNumbers
) {}
