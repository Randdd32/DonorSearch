package com.github.randdd32.donor_search_backend.web.dto.hardware;

import java.util.List;

public record PowerSupplyDto(
        Long id,
        String name,
        String manufacturerName,
        String typeName,
        String efficiencyName,
        String modularName,
        String colorName,

        Integer wattageW,
        Integer lengthMm,

        Integer atx4PinConnectors,
        Integer eps8PinConnectors,

        Integer pcie12vhpwrConnectors,
        Integer pcie12PinConnectors,
        Integer pcie8PinConnectors,
        Integer pcie6Plus2PinConnectors,
        Integer pcie6PinConnectors,

        Integer sataConnectors,
        Integer molex4PinConnectors,

        List<String> partNumbers
) {}
