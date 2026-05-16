package com.github.randdd32.donor_search_backend.service;

import com.github.randdd32.donor_search_backend.core.error.NotFoundException;
import com.github.randdd32.donor_search_backend.model.IntegrationMappingEntity;
import com.github.randdd32.donor_search_backend.model.enums.ComponentType;
import com.github.randdd32.donor_search_backend.model.enums.MappingConfidence;
import com.github.randdd32.donor_search_backend.model.hardware.ComponentEntity;
import com.github.randdd32.donor_search_backend.model.hardware.VideoCardEntity;
import com.github.randdd32.donor_search_backend.repository.IntegrationMappingRepository;
import com.github.randdd32.donor_search_backend.repository.hardware.ComponentScoreProjection;
import com.github.randdd32.donor_search_backend.service.hardware.ComponentService;
import com.github.randdd32.donor_search_backend.web.dto.filter.IntegrationMappingFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IntegrationMappingServiceTest {
    @Mock
    private IntegrationMappingRepository repository;

    @Mock
    private ComponentService componentService;

    @InjectMocks
    private IntegrationMappingService mappingService;

    private IntegrationMappingEntity mapping;
    private ComponentEntity mockComponent;

    @BeforeEach
    void setUp() {
        mockComponent = new VideoCardEntity();
        mockComponent.setId(10L);

        mapping = new IntegrationMappingEntity();
        mapping.setId(1L);
        mapping.setExternalName("NVIDIA RTX 3060");
        mapping.setInternalComponent(mockComponent);
        mapping.setConfidence(MappingConfidence.CONFIRMED);
    }

    @Test
    @DisplayName("Позитивный тест: получение маппинга по ID")
    void getById_Positive() {
        when(repository.findById(1L)).thenReturn(Optional.of(mapping));
        IntegrationMappingEntity found = mappingService.getById(1L);
        assertNotNull(found);
        assertEquals("NVIDIA RTX 3060", found.getExternalName());
    }

    @Test
    @DisplayName("Негативный тест: маппинг по ID не найден")
    void getById_Negative_NotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> mappingService.getById(99L));
    }

    @Test
    @DisplayName("Позитивный тест: поиск по externalName (с учетом регистра и пробелов)")
    void findByExternalName_Positive() {
        when(repository.findByExternalNameIgnoreCase("NVIDIA RTX 3060")).thenReturn(Optional.of(mapping));
        Optional<IntegrationMappingEntity> found = mappingService.findByExternalName("  NVIDIA RTX 3060  ");
        assertTrue(found.isPresent());
    }

    @Test
    @DisplayName("Позитивный тест: получение списка с фильтрацией и пагинацией (getAll)")
    void getAll_Positive() {
        Pageable pageable = PageRequest.of(0, 10);
        IntegrationMappingFilter filter = new IntegrationMappingFilter("RTX", MappingConfidence.AUTO, null,
                null, null, null, null);
        Page<IntegrationMappingEntity> page = new PageImpl<>(List.of(mapping));

        when(repository.findAll(Mockito.<Specification<IntegrationMappingEntity>>any(), eq(pageable))).thenReturn(page);

        Page<IntegrationMappingEntity> result = mappingService.getAll(filter, pageable);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Позитивный тест: получение словаря ID маппингов (getMappedComponentIds)")
    void getMappedComponentIds_Positive() {
        List<Object[]> rawData = Collections.singletonList(new Object[]{"nvidia rtx 3060", 10L});
        when(repository.findMappedIdsByNames(List.of("nvidia rtx 3060"))).thenReturn(rawData);

        Map<String, Long> result = mappingService.getMappedComponentIds(List.of("NVIDIA RTX 3060"));
        assertEquals(1, result.size());
        assertEquals(10L, result.get("nvidia rtx 3060"));
    }

    @Test
    @DisplayName("Позитивный тест: создание из DTO (createFromDto)")
    void createFromDto_Positive() {
        when(componentService.getById(10L)).thenReturn(mockComponent);
        when(repository.findByExternalNameIgnoreCase("NVIDIA RTX 3060")).thenReturn(Optional.empty());
        when(repository.save(any())).thenReturn(mapping);

        IntegrationMappingEntity created = mappingService.createFromDto(mapping, 10L);
        assertNotNull(created.getInternalComponent());
        verify(repository, times(1)).save(mapping);
    }

    @Test
    @DisplayName("Позитивный тест: обновление внутреннего компонента (updateInternalComponent)")
    void updateInternalComponent_Positive() {
        ComponentEntity newComponent = new VideoCardEntity();
        newComponent.setId(20L);

        when(repository.findById(1L)).thenReturn(Optional.of(mapping));
        when(componentService.getById(20L)).thenReturn(newComponent);
        when(repository.save(any())).thenReturn(mapping);

        IntegrationMappingEntity updated = mappingService.updateInternalComponent(1L, 20L, MappingConfidence.AUTO);

        assertEquals(20L, updated.getInternalComponent().getId());
        assertEquals(MappingConfidence.AUTO, updated.getConfidence());
    }

    @Test
    @DisplayName("Позитивный тест: удаление маппинга")
    void delete_Positive() {
        when(repository.findById(1L)).thenReturn(Optional.of(mapping));
        mappingService.delete(1L);
        verify(repository, times(1)).delete(mapping);
    }

    @Test
    @DisplayName("Позитивный тест: создание маппинга со статусом AUTO (схожесть >= 0.90)")
    void resolveAndSaveMapping_Positive_AutoConfidence() {
        String externalName = "RTX 3060";
        when(repository.findByExternalNameIgnoreCase(externalName)).thenReturn(Optional.empty());

        ComponentScoreProjection mockProjection = mock(ComponentScoreProjection.class);
        when(mockProjection.getScore()).thenReturn(0.95);
        when(mockProjection.getId()).thenReturn(1L);

        when(componentService.findBestMatchWithScore(externalName, ComponentType.VIDEO_CARD))
                .thenReturn(Optional.of(mockProjection));
        when(componentService.getById(1L)).thenReturn(mockComponent);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        IntegrationMappingEntity result = mappingService.resolveAndSaveMapping(externalName, ComponentType.VIDEO_CARD);

        assertNotNull(result);
        assertEquals(MappingConfidence.AUTO, result.getConfidence());
    }

    @Test
    @DisplayName("Позитивный тест: возврат существующего маппинга без повторного поиска")
    void resolveAndSaveMapping_Positive_ExistingMapping() {
        when(repository.findByExternalNameIgnoreCase("NVIDIA RTX 3060")).thenReturn(Optional.of(mapping));

        IntegrationMappingEntity result = mappingService.resolveAndSaveMapping("NVIDIA RTX 3060", ComponentType.VIDEO_CARD);

        assertNotNull(result);
        verify(componentService, never()).findBestMatchWithScore(anyString(), any());
    }

    @Test
    @DisplayName("Позитивный тест: массовое сопоставление (resolveAndSaveBatch) с кэшированием")
    void resolveAndSaveBatch_Positive() {
        Set<String> externalNames = Set.of("Known GPU", "Unknown GPU");

        IntegrationMappingEntity knownMapping = new IntegrationMappingEntity();
        knownMapping.setExternalName("Known GPU");
        knownMapping.setConfidence(MappingConfidence.CONFIRMED);

        when(repository.findMappingsByNamesWithComponents(anyList())).thenReturn(List.of(knownMapping));

        ComponentScoreProjection mockProjection = mock(ComponentScoreProjection.class);
        when(mockProjection.getScore()).thenReturn(0.70);
        when(mockProjection.getId()).thenReturn(20L);

        when(componentService.findBestMatchWithScore("unknown gpu", ComponentType.VIDEO_CARD)).thenReturn(Optional.of(mockProjection));
        when(componentService.getById(20L)).thenReturn(mockComponent);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Map<String, IntegrationMappingEntity> result = mappingService.resolveAndSaveBatch(externalNames, ComponentType.VIDEO_CARD);

        assertEquals(2, result.size());
        assertEquals(MappingConfidence.CONFIRMED, result.get("known gpu").getConfidence());
        assertEquals(MappingConfidence.NEEDS_REVIEW, result.get("unknown gpu").getConfidence());

        verify(componentService, times(1)).findBestMatchWithScore(anyString(), any());
    }

    @Test
    @DisplayName("Негативный тест: создание с пустым названием")
    void create_Negative_MissingExternalName() {
        mapping.setExternalName("");
        assertThrows(IllegalArgumentException.class, () -> mappingService.createFromDto(mapping, 10L));
    }

    @Test
    @DisplayName("Негативный тест: создание с дублирующимся названием (уже существует)")
    void validate_Negative_DuplicateExternalName() {
        IntegrationMappingEntity existing = new IntegrationMappingEntity();
        existing.setId(2L);
        existing.setExternalName("NVIDIA RTX 3060");

        when(repository.findByExternalNameIgnoreCase("NVIDIA RTX 3060")).thenReturn(Optional.of(existing));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> mappingService.update(1L, mapping));
        assertTrue(ex.getMessage().contains("already exists"));
    }
}
