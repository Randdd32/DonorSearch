package com.github.randdd32.donor_search_backend.web.dto.integration;

import java.util.List;

public record ExternalDeviceDto(
        Long externalId,
        String name,
        String inventoryNumber,
        String serialNumber,
        String lifeCycleState,
        String ownerFullName,
        String departmentName,
        String locationPath,
        List<ExternalComponentDto> components
) {}
