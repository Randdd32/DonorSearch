package com.github.randdd32.donor_search_backend.web.dto.hardware;

import java.util.List;

public record MonitorDto(
        Long id,
        String name,
        String manufacturerName,
        String resolutionName,
        String panelTypeName,
        String aspectRatioName,

        Double screenSizeIn,
        Integer refreshRateHz,
        Double responseTimeMs,

        Integer inputHdmi,
        Integer inputDp,
        Integer inputDvi,
        Integer inputVga,
        Integer inputUsbC,
        Integer inputMiniHdmi,
        Integer inputMicroHdmi,
        Integer inputMiniDp,
        Integer inputBnc,
        Integer inputComponent,
        Integer inputSVideo,
        Integer inputVirtualLink,

        List<String> partNumbers
) {}
