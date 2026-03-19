package com.github.randdd32.donor_search_backend.web.dto.search;

import com.github.randdd32.donor_search_backend.web.dto.search.enums.WarningSeverity;

public record DonorWarningDto(
        String message,
        WarningSeverity severity
) {}
