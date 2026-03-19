package com.github.randdd32.donor_search_backend.web.dto.integration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.randdd32.donor_search_backend.web.dto.integration.enums.ExternalDeviceState;

import java.time.Instant;
import java.util.List;

public record ExternalDeviceDto(
        Long externalId,
        String name,
        String inventoryNumber,
        String serialNumber,
        @JsonIgnore Long modelId,
        String modelName,
        @JsonIgnore Long manufacturerId,
        String manufacturerName,
        @JsonIgnore Long typeId,
        String typeName,
        @JsonIgnore Long stateId,
        ExternalDeviceState lifeCycleState,
        String ownerFullName,
        @JsonIgnore Long departmentId,
        String departmentName,
        @JsonIgnore Long buildingId,
        @JsonIgnore Long floorId,
        @JsonIgnore Long roomId,
        String locationPath,
        Instant dateReceived,
        Boolean isWorking,
        List<ExternalComponentDto> components
) {}
