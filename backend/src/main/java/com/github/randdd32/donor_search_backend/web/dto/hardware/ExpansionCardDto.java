package com.github.randdd32.donor_search_backend.web.dto.hardware;

import com.github.randdd32.donor_search_backend.model.enums.ExpansionCardType;

import java.util.List;

public record ExpansionCardDto(
        Long id,
        String name,
        String manufacturerName,
        ExpansionCardType cardType,
        String interfaceName,
        String colorName,

        String audioChipsetName,
        Double channels,
        Integer digitalAudioBit,
        Double sampleRateKhz,

        String protocolName,

        List<String> partNumbers
) {}
