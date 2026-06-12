package com.github.randdd32.donor_search_backend.web.dto.filter;

import com.github.randdd32.donor_search_backend.model.enums.ExpansionCardType;

import java.util.List;

public record ExpansionCardFilter(
        String search,
        ExpansionCardType cardType,
        List<Long> manufacturerIds,
        List<Long> interfaceIds,
        List<Long> colorIds,
        List<Long> audioChipsetIds,
        List<Long> protocolIds,
        Double minChannels,
        Double maxChannels,
        Integer minDigitalAudioBit,
        Integer maxDigitalAudioBit,
        Double minSampleRateKhz,
        Double maxSampleRateKhz
) {}
