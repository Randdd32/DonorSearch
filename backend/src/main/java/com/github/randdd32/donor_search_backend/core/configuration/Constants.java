package com.github.randdd32.donor_search_backend.core.configuration;

import java.util.List;

public final class Constants {
    public static final String API_URL = "/api/v1";

    public static final String COMPONENTS_URL = "/components";

    public static final String DICTIONARIES_URL = "/dictionaries";

    public static final String INFRA_URL = "/infra";

    public static final int DEFAULT_PAGE_SIZE = 20;

    public static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*_=+\\\\-]).{8,60}$";

    public static final List<String> ALLOWED_PC_TYPE_UUIDS = List.of(
            "99BAEB7D-7E92-43ED-A18F-B98B31B8299B", // Терминал
            "3D2E3D02-7D8D-40BA-8902-EB9B949A4529", // Сервер
            "980383F4-0699-4B57-B8F5-CD239363ABF7", // Моноблок
            "25F4E821-BD91-404B-98F4-CF4EC9483A37", // Ноутбук
            "316D5D9D-8884-4ED3-B277-73DA383F81B6", // Нетбук
            "3E1E48C8-D9D3-4B89-8C8F-39A76EAC779D"  // Промышленный ПК
    );

    // TODO: Заменить на UUID, когда посмотрю нужные
    public static final List<String> ALLOWED_STATE_NAMES = List.of(
            "Неучтенное",
            "Хранение",
            "Использование",
            "Ремонт",
            "Списано"
    );

    private Constants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
