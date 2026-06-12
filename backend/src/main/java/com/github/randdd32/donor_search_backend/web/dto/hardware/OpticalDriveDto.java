package com.github.randdd32.donor_search_backend.web.dto.hardware;

import java.util.List;

public record OpticalDriveDto(
        Long id,
        String name,
        String manufacturerName,
        String formFactorName,
        String interfaceName,
        Integer bdSpeedX,
        Integer dvdSpeedX,
        Integer cdSpeedX,
        List<String> partNumbers
) {}
