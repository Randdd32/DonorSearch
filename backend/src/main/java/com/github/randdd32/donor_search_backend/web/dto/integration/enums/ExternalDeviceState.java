package com.github.randdd32.donor_search_backend.web.dto.integration.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExternalDeviceState {
    WRITTEN_OFF("Списано", 0),
    STORAGE("Хранение", 1),
    UNACCOUNTED("Неучтенное", 20),
    REPAIR("Ремонт", 20),
    IN_USE("Использование", 50),
    UNKNOWN("Неизвестно", 20);

    private final String infraName;
    private final int penaltyWeight;

    public static ExternalDeviceState fromInfraName(String infraName) {
        if (infraName == null || infraName.isBlank()) return UNKNOWN;
        String lower = infraName.toLowerCase();
        if (lower.contains("списан")) return WRITTEN_OFF;
        if (lower.contains("хранен")) return STORAGE;
        if (lower.contains("неучтен")) return UNACCOUNTED;
        if (lower.contains("ремонт")) return REPAIR;
        if (lower.contains("использов")) return IN_USE;
        return UNKNOWN;
    }
}
