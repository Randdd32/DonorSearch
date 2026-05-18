package com.github.randdd32.donor_search_backend.integration;

import com.github.randdd32.donor_search_backend.service.integration.dictionary.*;
import com.github.randdd32.donor_search_backend.web.dto.dictionary.InfraDictionaryDto;
import com.github.randdd32.donor_search_backend.web.dto.pagination.PageDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
class InfraDictionariesIntegrationTest {
    @Autowired
    private InfraBuildingService buildingService;
    @Autowired
    private InfraDepartmentService departmentService;
    @Autowired
    private InfraDeviceModelService deviceModelService;
    @Autowired
    private InfraDeviceTypeService deviceTypeService;
    @Autowired
    private InfraFloorService floorService;
    @Autowired
    private InfraManufacturerService manufacturerService;
    @Autowired
    private InfraRoomService roomService;
    @Autowired
    private InfraStateService stateService;

    @Test
    @DisplayName("Позитивный тест: извлечение данных из всех справочников 'ИнфраМенеджера'")
    void fetchAllDictionaries_Positive() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        PageDto<InfraDictionaryDto> buildings = buildingService.search(null, null, pageRequest);
        assertTrue(buildings.totalItems() > 0, "Справочник зданий пуст");

        PageDto<InfraDictionaryDto> departments = departmentService.search(null, null, pageRequest);
        assertTrue(departments.totalItems() > 0, "Справочник отделов пуст");

        PageDto<InfraDictionaryDto> models = deviceModelService.search(null, null, pageRequest);
        assertTrue(models.totalItems() > 0, "Справочник моделей пуст");

        PageDto<InfraDictionaryDto> types = deviceTypeService.search(null, null, pageRequest);
        assertTrue(types.totalItems() > 0, "Справочник типов пуст");

        PageDto<InfraDictionaryDto> floors = floorService.search(null, null, pageRequest);
        assertTrue(floors.totalItems() > 0, "Справочник этажей пуст");

        PageDto<InfraDictionaryDto> manufacturers = manufacturerService.search(null, null, pageRequest);
        assertTrue(manufacturers.totalItems() > 0, "Справочник производителей пуст");

        PageDto<InfraDictionaryDto> rooms = roomService.search(null, null, pageRequest);
        assertTrue(rooms.totalItems() > 0, "Справочник комнат пуст");

        PageDto<InfraDictionaryDto> states = stateService.search(null, null, pageRequest);
        assertTrue(states.totalItems() > 0, "Справочник статусов пуст");
    }

    @Test
    @DisplayName("Позитивный тест: поиск и фильтрация по ID (getByIds)")
    void getByIds_Dictionaries_Positive() {
        // Тестируем получение статуса "Хранение" по конкретному ID
        String storageStateId = "9A1D33B2-01BF-4847-90F1-B5D6A5A9EDEB";
        List<InfraDictionaryDto> states = stateService.getByIds(List.of(storageStateId));

        assertFalse(states.isEmpty());
        assertEquals("Хранение", states.get(0).name());
    }

    @Test
    @DisplayName("Позитивный тест: поиск с текстовым фильтром")
    void searchWithTextFilter_Positive() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        PageDto<InfraDictionaryDto> models = deviceModelService.search("ProDesk", null, pageRequest);

        assertTrue(models.totalItems() > 0);
        assertTrue(models.items().get(0).name().contains("ProDesk"));
    }
}
