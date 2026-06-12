package com.github.randdd32.donor_search_backend.web.dto.integration.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    private static final Map<String, ExternalDeviceState> EXACT_MATCH_MAP =
            Arrays.stream(values())
                    .collect(Collectors.toMap(
                            s -> s.getInfraName().toLowerCase(),
                            Function.identity()
                    ));

    public static ExternalDeviceState fromInfraName(String infraName) {
        if (infraName == null || infraName.isBlank()) {
            return UNKNOWN;
        }
        return EXACT_MATCH_MAP.getOrDefault(infraName.trim().toLowerCase(), UNKNOWN);
    }
}
