package com.github.randdd32.donor_search_backend.web.dto.hardware;

import com.github.randdd32.donor_search_backend.model.hardware.nested.M2Slot;

import java.util.List;

public record MotherboardDto(
        Long id,
        String name,
        String manufacturerName,
        String socketName,
        String formFactorName,
        String memoryTypeName,
        String colorName,

        Integer maxMemoryGb,
        Integer memorySlots,
        Integer memorySpeedMaxMhz,
        Boolean eccSupport,
        Boolean usesBackConnect,

        List<M2Slot> m2Slots,

        Integer sata6Ports,
        Integer sata3Ports,
        Integer pciX16Slots,
        Integer pciX8Slots,
        Integer pciX4Slots,
        Integer pciX1Slots,
        Integer pciSlots,
        Integer miniPcieMsataSlots,

        Integer headerUsb20,
        Integer headerUsb32Gen1,
        Integer headerUsb32Gen2,
        Integer headerUsb32Gen2x2,
        Integer headerUsb20SinglePort,

        List<String> partNumbers
) {}
