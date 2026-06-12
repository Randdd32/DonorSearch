package com.github.randdd32.donor_search_backend.service.search;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.randdd32.donor_search_backend.core.error.NotFoundException;
import com.github.randdd32.donor_search_backend.model.IntegrationMappingEntity;
import com.github.randdd32.donor_search_backend.model.enums.ComponentType;
import com.github.randdd32.donor_search_backend.model.enums.MappingConfidence;
import com.github.randdd32.donor_search_backend.model.hardware.CpuEntity;
import com.github.randdd32.donor_search_backend.service.IntegrationMappingService;
import com.github.randdd32.donor_search_backend.service.compatibility.CompatibilityEngineService;
import com.github.randdd32.donor_search_backend.service.integration.InfraDeviceService;
import com.github.randdd32.donor_search_backend.web.dto.filter.DonorSearchFilter;
import com.github.randdd32.donor_search_backend.web.dto.integration.ExternalComponentDto;
import com.github.randdd32.donor_search_backend.web.dto.integration.ExternalDeviceDto;
import com.github.randdd32.donor_search_backend.web.dto.integration.enums.ExternalComponentCategory;
import com.github.randdd32.donor_search_backend.web.dto.integration.enums.ExternalDeviceState;
import com.github.randdd32.donor_search_backend.web.dto.pagination.PageDto;
import com.github.randdd32.donor_search_backend.web.dto.search.DonorResultDto;
import lombok.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DonorSearchServiceTest {
    @Mock
    private InfraDeviceService infraDeviceService;
    @Mock
    private IntegrationMappingService mappingService;
    @Mock
    private CompatibilityEngineService compatibilityEngine;
    @Mock
    private Cache<@NonNull String, List<DonorResultDto>> searchCache;

    @Captor
    private ArgumentCaptor<List<DonorResultDto>> donorResultCaptor;

    @InjectMocks
    private DonorSearchService donorSearchService;

    private ExternalDeviceDto mockTargetDevice;

    @BeforeEach
    void setUp() {
        mockTargetDevice = new ExternalDeviceDto(
                1L, "Target PC", "INV-01", "SN-01", null, null, null, null,
                1L, "Model", "PN", null, 1L, "Manuf", "typeId", "PC", "stateId",
                ExternalDeviceState.IN_USE, "Owner", "Phone", "Pos", "DeptId", "Dept",
                1L, 1L, 1L, "Path", Instant.now(), true, BigDecimal.TEN, null, null,
                null, null, null, null,
                List.of(new ExternalComponentDto("adapter123", "cat1", "Intel CPU",
                        ExternalComponentCategory.CPU, 1L, "Intel", "sn", null, null, null, null, 1L))
        );
    }

    @Test
    @DisplayName("Позитивный тест: успешный запуск поиска донора (runSearch) и сохранение в кэш")
    void runSearch_Positive() {
        when(infraDeviceService.getDeviceDetails(1L)).thenReturn(mockTargetDevice);

        when(infraDeviceService.getPotentialDonors(eq(1L), eq(ExternalComponentCategory.CPU))).thenReturn(Collections.emptyList());
        when(mappingService.resolveAndSaveBatch(any(), eq(ComponentType.CPU))).thenReturn(Collections.emptyMap());

        String sessionId = donorSearchService.runSearch(1L, "adapter123", null);

        assertNotNull(sessionId);
        verify(searchCache, times(1)).put(eq(sessionId), any());
    }

    @Test
    @DisplayName("Позитивный тест: успешный подбор донора и корректный расчет штрафов")
    void runSearch_Positive_CalculatesPenaltiesCorrectly() {
        ExternalComponentDto donorCpu = new ExternalComponentDto(
                "adapter999", "cat1", "AMD Ryzen 5", ExternalComponentCategory.CPU,
                2L, "AMD", "sn2", null, null, null, null, 2L);

        // Статус STORAGE: согласно ExternalDeviceState.STORAGE вес штрафа = 1
        ExternalDeviceDto donorDevice = new ExternalDeviceDto(
                2L, "Donor PC", "INV-02", "SN-02", null, null, null, null,
                1L, "Model", "PN", null, 1L, "Manuf", "typeId", "PC", "stateId",
                ExternalDeviceState.STORAGE,
                "Owner", "Phone", "Pos", "DeptId", "Dept",
                1L, 1L, 1L, "Path", Instant.now(), true, BigDecimal.TEN, null, null,
                null, null, null, null,
                List.of(donorCpu)
        );

        when(infraDeviceService.getDeviceDetails(1L)).thenReturn(mockTargetDevice);
        when(infraDeviceService.getPotentialDonors(1L, ExternalComponentCategory.CPU)).thenReturn(List.of(donorDevice));

        // Настраиваем маппинг для донорской детали
        // Статус AUTO: вес штрафа = 1
        IntegrationMappingEntity mockMapping = new IntegrationMappingEntity();
        mockMapping.setExternalName("AMD Ryzen 5");
        mockMapping.setConfidence(MappingConfidence.AUTO);

        CpuEntity internalCpu = new CpuEntity();
        mockMapping.setInternalComponent(internalCpu);

        when(mappingService.resolveAndSaveBatch(any(), eq(ComponentType.CPU)))
                .thenReturn(Map.of("amd ryzen 5", mockMapping));

        // Движок совместимости не возвращает ошибок: вес штрафа = 0
        when(compatibilityEngine.evaluateCompatibility(any(), eq(ComponentType.CPU)))
                .thenReturn(Collections.emptyList());

        String sessionId = donorSearchService.runSearch(1L, "adapter123", null);

        verify(searchCache).put(eq(sessionId), donorResultCaptor.capture());

        List<DonorResultDto> results = donorResultCaptor.getValue();
        assertEquals(1, results.size());

        DonorResultDto result = results.get(0);
        assertEquals(2L, result.donorDevice().externalId());

        // Проверка правильности расчетов:
        // 1. Штраф за статус устройства STORAGE = 1
        assertEquals(1, result.devicePenalty(), "Неверный штраф за устройство");

        // 2. Штраф компонента = 1 (за маппинг AUTO) + 0 (предупреждения правил) = 1
        assertEquals(1, result.compatibleComponents().get(0).componentPenalty(), "Неверный штраф за компонент");

        // 3. Итоговый штраф = 1 + 1 = 2
        assertEquals(2, result.totalPenalty(), "Неверный итоговый штраф");
    }

    @Test
    @DisplayName("Негативный тест: запуск поиска без указания адаптера или категории")
    void runSearch_Negative_MissingAdapterAndCategory() {
        when(infraDeviceService.getDeviceDetails(1L)).thenReturn(mockTargetDevice);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                donorSearchService.runSearch(1L, null, null)
        );
        assertEquals("Must provide either targetAdapterId or category", ex.getMessage());
    }

    @Test
    @DisplayName("Негативный тест: попытка запустить поиск для адаптера, которого нет в целевом ПК")
    void runSearch_Negative_AdapterNotFoundInDevice() {
        when(infraDeviceService.getDeviceDetails(1L)).thenReturn(mockTargetDevice);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                donorSearchService.runSearch(1L, "invalid-adapter", null)
        );
        assertEquals("Target adapter not found in device", ex.getMessage());
    }

    @Test
    @DisplayName("Позитивный тест: получение результатов из кэша с фильтрацией (getResults)")
    void getResults_Positive() {
        String sessionId = "test-session";
        DonorResultDto resultDto = new DonorResultDto(mockTargetDevice, 50, 50, Collections.emptyList());
        when(searchCache.getIfPresent(sessionId)).thenReturn(List.of(resultDto));

        DonorSearchFilter filter = new DonorSearchFilter("Target", null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null);
        Pageable pageable = PageRequest.of(0, 10);

        PageDto<DonorResultDto> page = donorSearchService.getResults(sessionId, filter, pageable);

        assertEquals(1, page.totalItems());
        assertEquals("Target PC", page.items().get(0).donorDevice().name());
    }

    @Test
    @DisplayName("Позитивный тест: фильтрация отсеивает результаты (ничего не найдено)")
    void getResults_Positive_FilterExcludes() {
        String sessionId = "test-session";
        DonorResultDto resultDto = new DonorResultDto(mockTargetDevice, 50, 50, Collections.emptyList());
        when(searchCache.getIfPresent(sessionId)).thenReturn(List.of(resultDto));

        DonorSearchFilter filter = new DonorSearchFilter("INV-999", null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null);
        Pageable pageable = PageRequest.of(0, 10);

        PageDto<DonorResultDto> page = donorSearchService.getResults(sessionId, filter, pageable);

        assertEquals(0, page.totalItems());
    }

    @Test
    @DisplayName("Негативный тест: получение результатов для просроченной (несуществующей) сессии")
    void getResults_Negative_SessionNotFound() {
        when(searchCache.getIfPresent("invalid-session")).thenReturn(null);

        DonorSearchFilter filter = new DonorSearchFilter(null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null);
        Pageable pageable = PageRequest.of(0, 10);

        NotFoundException ex = assertThrows(NotFoundException.class, () ->
                donorSearchService.getResults("invalid-session", filter, pageable)
        );
        assertTrue(ex.getMessage().contains("has expired or not found"));
    }
}
