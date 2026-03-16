package com.github.randdd32.donor_search_backend.web.dto.integration.enums;

import lombok.Getter;

@Getter
public enum ExternalComponentCategory {
    CPU("Процессор"),
    CPU_COOLER("Кулер"),
    MOTHERBOARD("Материнская плата"),
    GPU("Видеоадаптер"),
    RAM("Память"),
    STORAGE("Жесткий диск"),
    OPTICAL_DRIVE("DVD привод"),
    POWER_SUPPLY("Блок питания"),
    CASE("Корпус"),
    CASE_FAN("Корпусной вентилятор"),
    EXPANSION_CARD("Сетевой адаптер"),
    UNKNOWN("Неизвестно");

    private final String infraName;

    ExternalComponentCategory(String infraName) {
        this.infraName = infraName;
    }

    public static ExternalComponentCategory fromInfraName(String infraName) {
        if (infraName == null || infraName.isBlank()) {
            return UNKNOWN;
        }
        for (ExternalComponentCategory category : values()) {
            if (infraName.toLowerCase().contains(category.infraName.toLowerCase())) {
                return category;
            }
        }
        return UNKNOWN;
    }
}
