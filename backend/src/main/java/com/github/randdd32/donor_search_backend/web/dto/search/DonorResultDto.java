package com.github.randdd32.donor_search_backend.web.dto.search;

import com.github.randdd32.donor_search_backend.web.dto.integration.ExternalDeviceDto;

import java.util.List;

public record DonorResultDto(
        ExternalDeviceDto donorDevice,
        int devicePenalty,
        int totalPenalty,
        List<CompatibleComponentDto> compatibleComponents
) {}
