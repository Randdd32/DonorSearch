package com.github.randdd32.donor_search_backend.service.compatibility;

import com.github.randdd32.donor_search_backend.core.error.NotFoundException;
import com.github.randdd32.donor_search_backend.model.compatibility.CompatibilityRuleEntity;
import com.github.randdd32.donor_search_backend.model.enums.ComponentType;
import com.github.randdd32.donor_search_backend.repository.compatibility.CompatibilityRuleRepository;
import com.github.randdd32.donor_search_backend.web.dto.filter.CompatibilityRuleFilter;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompatibilityRuleServiceTest {
    @Mock
    private CompatibilityRuleRepository repository;

    @InjectMocks
    private CompatibilityRuleService compatibilityRuleService;

    private CompatibilityRuleEntity rule;

    @BeforeEach
    void setUp() {
        rule = new CompatibilityRuleEntity();
        rule.setId(1L);
        rule.setRuleCode("UNIQUE_CODE");
        rule.setRuleName("Test Rule");
        rule.setExpression("#ctx.getTotalTdpW() > 0");
        rule.setErrorMessage("Error");
        rule.setIsActive(true);
        rule.getTargetComponentTypes().add(ComponentType.CPU);
    }

    @Test
    @DisplayName("Позитивный тест: получение правила по ID")
    void getById_Positive() {
        when(repository.findById(1L)).thenReturn(Optional.of(rule));
        CompatibilityRuleEntity found = compatibilityRuleService.getById(1L);
        assertNotNull(found);
        assertEquals(1L, found.getId());
    }

    @Test
    @DisplayName("Негативный тест: получение правила по несуществующему ID (NotFoundException)")
    void getById_Negative_NotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> compatibilityRuleService.getById(99L));
    }

    @Test
    @DisplayName("Позитивный тест: получение списка с фильтрацией и пагинацией (getAll)")
    void getAll_Positive() {
        Pageable pageable = PageRequest.of(0, 10);
        CompatibilityRuleFilter filter = new CompatibilityRuleFilter("Test", true, null,
                null, null, null, null);
        Page<CompatibilityRuleEntity> page = new PageImpl<>(List.of(rule));

        when(repository.findAll(Mockito.<Specification<CompatibilityRuleEntity>>any(), eq(pageable))).thenReturn(page);

        Page<CompatibilityRuleEntity> result = compatibilityRuleService.getAll(filter, pageable);
        assertEquals(1, result.getTotalElements());
        verify(repository, times(1)).findAll(Mockito.<Specification<CompatibilityRuleEntity>>any(), eq(pageable));
    }

    @Test
    @DisplayName("Позитивный тест: получение активных правил по типу компонента")
    void getActiveRulesByComponentType_Positive() {
        when(repository.findAll(Mockito.<Specification<CompatibilityRuleEntity>>any())).thenReturn(List.of(rule));

        List<CompatibilityRuleEntity> rules = compatibilityRuleService.getActiveRulesByComponentType(ComponentType.CPU);
        assertEquals(1, rules.size());
        assertEquals(ComponentType.CPU, rules.get(0).getTargetComponentTypes().iterator().next());
    }

    @Test
    @DisplayName("Позитивный тест: подсчет количества правил (count)")
    void count_Positive() {
        when(repository.count()).thenReturn(5L);
        long count = compatibilityRuleService.count();
        assertEquals(5L, count);
    }

    @Test
    @DisplayName("Позитивный тест: успешное создание правила (create)")
    void create_Positive() {
        when(repository.findByRuleCodeIgnoreCase("UNIQUE_CODE")).thenReturn(Optional.empty());
        when(repository.save(any())).thenReturn(rule);

        CompatibilityRuleEntity created = compatibilityRuleService.create(rule);
        assertNotNull(created);
        verify(repository, times(1)).save(rule);
    }

    @Test
    @DisplayName("Позитивный тест: успешное обновление правила (update)")
    void update_Positive() {
        String updatedRuleCode = "TEST_CODE";
        String updatedName = "Updated Name";
        String updatedExpression = "1 == 1";
        String updatedErrorMessage = "New Error";
        Boolean updatedIsActive = false;
        String updatedDescription = "Updated Description";
        ComponentType updatedComponentType = ComponentType.CASE;

        CompatibilityRuleEntity updatedRule = new CompatibilityRuleEntity();
        updatedRule.setRuleCode(updatedRuleCode);
        updatedRule.setRuleName(updatedName);
        updatedRule.setExpression(updatedExpression);
        updatedRule.setErrorMessage(updatedErrorMessage);
        updatedRule.setIsActive(updatedIsActive);
        updatedRule.setDescription(updatedDescription);
        updatedRule.getTargetComponentTypes().add(updatedComponentType);

        when(repository.findById(1L)).thenReturn(Optional.of(rule));
        when(repository.findByRuleCodeIgnoreCase(updatedRuleCode)).thenReturn(Optional.of(rule));
        when(repository.save(any())).thenReturn(rule);

        CompatibilityRuleEntity saved = compatibilityRuleService.update(1L, updatedRule);

        assertEquals(updatedRuleCode, saved.getRuleCode());
        assertEquals(updatedName, saved.getRuleName());
        assertEquals(updatedExpression, saved.getExpression());
        assertEquals(updatedErrorMessage, saved.getErrorMessage());
        assertEquals(updatedIsActive, saved.getIsActive());
        assertEquals(updatedDescription, saved.getDescription());
        assertTrue(saved.getTargetComponentTypes().contains(updatedComponentType));
        assertEquals(1, saved.getTargetComponentTypes().size());
        verify(repository, times(1)).save(rule);
    }

    @Test
    @DisplayName("Позитивный тест: успешное удаление правила (delete)")
    void delete_Positive() {
        when(repository.findById(1L)).thenReturn(Optional.of(rule));
        compatibilityRuleService.delete(1L);
        verify(repository, times(1)).delete(rule);
    }

    @Test
    @DisplayName("Позитивный тест: успешная проверка синтаксиса SpEL")
    void validateExpressionSyntax_Positive() {
        assertDoesNotThrow(() -> compatibilityRuleService.validateExpressionSyntax("#ctx.getTotalTdpW() > 100"));
    }

    @Test
    @DisplayName("Негативный тест: ошибка парсинга при неверном синтаксисе SpEL")
    void validateExpressionSyntax_Negative_SpelParseException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> compatibilityRuleService.validateExpressionSyntax("1 > = 2"));
        assertTrue(ex.getMessage().contains("Syntax error in the rule"));
    }

    @Test
    @DisplayName("Негативный тест: попытка создать правило без целевых компонентов")
    void create_Negative_MissingTargetComponents() {
        rule.getTargetComponentTypes().clear();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> compatibilityRuleService.create(rule));
        assertEquals("Target component types must not be empty", ex.getMessage());
    }

    @Test
    @DisplayName("Негативный тест: попытка создать правило с пустым кодом (пустая строка)")
    void create_Negative_EmptyRuleCode() {
        rule.setRuleCode("");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> compatibilityRuleService.create(rule));
        assertTrue(ex.getMessage().contains("Rule code must not be null or empty"));
    }

    @Test
    @DisplayName("Негативный тест: попытка создания правила с существующим кодом (дубликат)")
    void create_Negative_DuplicateRuleCode() {
        CompatibilityRuleEntity existingRule = new CompatibilityRuleEntity();
        existingRule.setId(2L);
        existingRule.setRuleCode("UNIQUE_CODE");

        when(repository.findByRuleCodeIgnoreCase("UNIQUE_CODE")).thenReturn(Optional.of(existingRule));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> compatibilityRuleService.create(rule));
        assertEquals("Compatibility rule with code 'UNIQUE_CODE' already exists", ex.getMessage());
    }

    @Test
    @DisplayName("Негативный тест: попытка обновления правила на код, занятый другим правилом")
    void update_Negative_DuplicateRuleCode() {
        CompatibilityRuleEntity existingRule = new CompatibilityRuleEntity();
        existingRule.setId(2L);
        existingRule.setRuleCode("UNIQUE_CODE");

        when(repository.findByRuleCodeIgnoreCase("UNIQUE_CODE")).thenReturn(Optional.of(existingRule));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> compatibilityRuleService.update(1L, rule));
        assertEquals("Compatibility rule with code 'UNIQUE_CODE' already exists", ex.getMessage());
    }
}
