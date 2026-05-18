package com.github.randdd32.donor_search_backend.integration;

import com.github.randdd32.donor_search_backend.core.error.NotFoundException;
import com.github.randdd32.donor_search_backend.service.integration.InfraDeviceService;
import com.github.randdd32.donor_search_backend.web.dto.filter.InfraDeviceFilter;
import com.github.randdd32.donor_search_backend.web.dto.integration.ExternalDeviceDto;
import com.github.randdd32.donor_search_backend.web.dto.integration.enums.ExternalComponentCategory;
import com.github.randdd32.donor_search_backend.web.dto.integration.enums.ExternalDeviceState;
import com.github.randdd32.donor_search_backend.web.dto.pagination.PageDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
class InfraDeviceServiceIntegrationTest {
    @Autowired
    private InfraDeviceService infraDeviceService;
    @Autowired
    private NamedParameterJdbcTemplate msSqlJdbcTemplate;

    @Test
    @DisplayName("Позитивный тест: успешная установка соединения с внешней БД и аутентификация")
    void testDatabaseConnection_Positive() {
        Integer result = msSqlJdbcTemplate.queryForObject("SELECT 1", new HashMap<>(), Integer.class);

        assertNotNull(result);
        assertEquals(1, result);
    }

    @Test
    @DisplayName("Позитивный тест: выполнение сложного нативного SQL для получения страницы устройств")
    void getDevicesPage_Positive() {
        // Ищем устройства со статусом "Использование" (C101D9C1-E716-44D4-A7DE-C8BAEDD5EF02)
        InfraDeviceFilter filter = new InfraDeviceFilter(
                null, List.of("C101D9C1-E716-44D4-A7DE-C8BAEDD5EF02"), null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null
        );

        PageDto<ExternalDeviceDto> page = infraDeviceService.getDevicesPage(filter, PageRequest.of(0, 10));

        assertNotNull(page);
        assertTrue(page.totalItems() > 0, "Должны найтись устройства в эксплуатации");

        ExternalDeviceDto device = page.items().get(0);
        assertNotNull(device.externalId());
        assertNotNull(device.name());
        assertEquals(ExternalDeviceState.IN_USE, device.lifeCycleState());
    }

    @Test
    @DisplayName("Позитивный тест: получение детальной информации об устройстве (JOIN с адаптерами)")
    void getDeviceDetails_Positive() {
        ExternalDeviceDto device = infraDeviceService.getDeviceDetails(1L);

        assertNotNull(device);
        assertEquals("PC-DEV-01", device.name());
        assertEquals("INV-001", device.inventoryNumber());
        assertEquals("Иванов Иван Иванович", device.ownerFullName());
        assertFalse(device.components().isEmpty(), "Компоненты устройства не должны быть пустыми");
    }

    @Test
    @DisplayName("Негативный тест: запрос детальной информации о несуществующем устройстве")
    void getDeviceDetails_Negative_NotFound() {
        assertThrows(NotFoundException.class, () -> infraDeviceService.getDeviceDetails(99999L));
    }

    @Test
    @DisplayName("Позитивный тест: поиск потенциальных доноров с отсечением исходного ПК")
    void getPotentialDonors_Positive() {
        List<ExternalDeviceDto> donors = infraDeviceService.getPotentialDonors(1L, ExternalComponentCategory.VIDEO_CARD);

        assertNotNull(donors);
        assertTrue(donors.stream().noneMatch(d -> d.externalId() == 1L));

        assertFalse(donors.isEmpty(), "Список потенциальных доноров не должен быть пуст");

        ExternalDeviceDto donor = donors.get(0);
        boolean hasGpu = donor.components().stream()
                .anyMatch(c -> c.category() == ExternalComponentCategory.VIDEO_CARD);
        assertTrue(hasGpu, "У найденного донора должна быть видеокарта");
    }
}
