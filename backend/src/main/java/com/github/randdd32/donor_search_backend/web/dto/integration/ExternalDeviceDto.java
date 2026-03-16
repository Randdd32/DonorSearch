package com.github.randdd32.donor_search_backend.web.dto.integration;

import java.time.Instant;
import java.util.List;

public record ExternalDeviceDto(
        Long externalId,
        String name,
        String inventoryNumber,
        String serialNumber,
        String modelName,
        String manufacturerName,
        String typeName,
        String lifeCycleState,
        String ownerFullName,
        String departmentName,
        String locationPath,
        Instant dateReceived,
        Boolean isWorking,
        List<ExternalComponentDto> components
) {}
