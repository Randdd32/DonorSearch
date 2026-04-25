package com.github.randdd32.donor_search_backend.web.dto.integration.enums;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum ExternalComponentCategory {
    CPU, CPU_COOLER, MOTHERBOARD, VIDEO_CARD, MEMORY, STORAGE,
    OPTICAL_DRIVE, POWER_SUPPLY, CASE, CASE_FAN, EXPANSION_CARD,
    MONITOR, UNKNOWN;

    private static final Map<String, ExternalComponentCategory> EXACT_MATCH_MAP = Map.ofEntries(
            Map.entry("процессор", CPU),
            Map.entry("материнская плата", MOTHERBOARD),
            Map.entry("жесткий диск", STORAGE),
            Map.entry("cd/dvd привод", OPTICAL_DRIVE),
            Map.entry("сетевая карта", EXPANSION_CARD),
            Map.entry("звуковая карта", EXPANSION_CARD),
            Map.entry("видеоадаптер", VIDEO_CARD),
            Map.entry("модуль оперативной памяти", MEMORY),
            Map.entry("монитор", MONITOR)
    );

    private static final Map<ExternalComponentCategory, List<String>> REVERSE_MAP;

    static {
        Map<ExternalComponentCategory, List<String>> temp = new EnumMap<>(ExternalComponentCategory.class);

        for (var entry : EXACT_MATCH_MAP.entrySet()) {
            temp.computeIfAbsent(entry.getValue(), k -> new ArrayList<>())
                    .add(entry.getKey());
        }

        REVERSE_MAP = temp.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(
                        Map.Entry::getKey,
                        e -> List.copyOf(e.getValue())
                ));
    }

    public static ExternalComponentCategory fromInfraName(String infraName) {
        if (infraName == null || infraName.isBlank()) {
            return UNKNOWN;
        }
        return EXACT_MATCH_MAP.getOrDefault(infraName.trim().toLowerCase(), UNKNOWN);
    }

    public List<String> getInfraNames() {
        return REVERSE_MAP.getOrDefault(this, List.of());
    }
}
