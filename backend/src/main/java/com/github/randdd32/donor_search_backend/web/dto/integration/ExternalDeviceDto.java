package com.github.randdd32.donor_search_backend.web.dto.integration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.randdd32.donor_search_backend.web.dto.integration.enums.ExternalDeviceState;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record ExternalDeviceDto(
        Long externalId,
        String name,
        String inventoryNumber,
        String serialNumber,
        String note,
        String assetTag,
        String code,
        String description,
        @JsonIgnore Long modelId,
        String modelName,
        String modelProductNumber,
        String modelNote,
        @JsonIgnore Long manufacturerId,
        String manufacturerName,
        @JsonIgnore String typeId,
        String typeName,
        @JsonIgnore String stateId,
        ExternalDeviceState lifeCycleState,
        String ownerFullName,
        String ownerPhone,
        String ownerPosition,
        @JsonIgnore String departmentId,
        String departmentName,
        @JsonIgnore Long buildingId,
        @JsonIgnore Long floorId,
        @JsonIgnore Long roomId,
        String locationPath,
        Instant dateReceived,
        Boolean isWorking,
        BigDecimal cost,
        String pcComposition,
        String ownershipNote,
        Instant dateInquiry,
        Instant appointmentDate,
        Instant dateAnnuled,
        String organizationName,
        List<ExternalComponentDto> components
) {}
