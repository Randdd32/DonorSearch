package com.github.randdd32.donor_search_backend.web.dto.search.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WarningSeverity {
    INFO(0),
    LOW(1),
    MEDIUM(10),
    HIGH(20),
    CRITICAL(50);

    private final int weight;
}
