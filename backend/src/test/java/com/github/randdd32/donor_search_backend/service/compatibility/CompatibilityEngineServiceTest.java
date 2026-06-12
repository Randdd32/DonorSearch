package com.github.randdd32.donor_search_backend.service.compatibility;

import com.github.randdd32.donor_search_backend.core.error.HardRejectException;
import com.github.randdd32.donor_search_backend.model.compatibility.CompatibilityRuleEntity;
import com.github.randdd32.donor_search_backend.model.enums.ComponentType;
import com.github.randdd32.donor_search_backend.service.compatibility.context.PcBuildContext;
import com.github.randdd32.donor_search_backend.web.dto.search.DonorWarningDto;
import com.github.randdd32.donor_search_backend.web.dto.search.enums.WarningSeverity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompatibilityEngineServiceTest {
    @Mock
    private CompatibilityRuleService ruleService;

    @InjectMocks
    private CompatibilityEngineService compatibilityEngineService;

    private PcBuildContext context;
    private CompatibilityRuleEntity rule;

    @BeforeEach
    void setUp() {
        context = new PcBuildContext();
        rule = new CompatibilityRuleEntity();
        rule.setRuleCode("TEST_RULE");
        rule.setRuleName("Тестовое правило");
        rule.setErrorMessage("Тестовая ошибка");
    }

    @Test
    @DisplayName("Позитивный тест: правило выполняется успешно (возвращает true)")
    void evaluateCompatibility_Positive_RulePassed() {
        rule.setExpression("1 == 1");
        when(ruleService.getActiveRulesByComponentType(ComponentType.VIDEO_CARD)).thenReturn(List.of(rule));

        List<DonorWarningDto> warnings = compatibilityEngineService.evaluateCompatibility(context, ComponentType.VIDEO_CARD);

        assertTrue(warnings.isEmpty(), "Список предупреждений должен быть пуст");
        verify(ruleService, times(1)).getActiveRulesByComponentType(ComponentType.VIDEO_CARD);
    }

    @Test
    @DisplayName("Негативный тест: правило не выполняется (возвращает false) -> HardRejectException")
    void evaluateCompatibility_Negative_RuleFailed_ThrowsHardReject() {
        rule.setExpression("1 == 2");
        when(ruleService.getActiveRulesByComponentType(ComponentType.VIDEO_CARD)).thenReturn(List.of(rule));

        HardRejectException exception = assertThrows(HardRejectException.class, () ->
                compatibilityEngineService.evaluateCompatibility(context, ComponentType.VIDEO_CARD)
        );

        assertEquals("Тестовая ошибка", exception.getMessage());
    }

    @Test
    @DisplayName("Негативный тест: нехватка данных контекста (вызов метода, который выбрасывает MissingContextDataException)")
    void evaluateCompatibility_Negative_MissingContextDataMethod() {
        rule.setExpression("#ctx.requireCpus().size() > 0");
        rule.setDescription("Описание правила для логов");
        when(ruleService.getActiveRulesByComponentType(ComponentType.MOTHERBOARD)).thenReturn(List.of(rule));

        List<DonorWarningDto> warnings = compatibilityEngineService.evaluateCompatibility(context, ComponentType.MOTHERBOARD);

        assertEquals(1, warnings.size());
        DonorWarningDto warning = warnings.get(0);
        assertEquals(WarningSeverity.HIGH, warning.severity());
        assertTrue(warning.message().contains("Не удалось проверить правило «Тестовое правило»"));
        assertTrue(warning.message().contains("нет данных о процессорах"));
    }

    @Test
    @DisplayName("Негативный тест: обращение к null полю (SpEL PROPERTY_OR_FIELD_NOT_READABLE_ON_NULL)")
    void evaluateCompatibility_Negative_MissingDataViaNullPointer() {
        rule.setExpression("#ctx.pcCase.lengthMm > 100");
        when(ruleService.getActiveRulesByComponentType(ComponentType.VIDEO_CARD)).thenReturn(List.of(rule));

        List<DonorWarningDto> warnings = compatibilityEngineService.evaluateCompatibility(context, ComponentType.VIDEO_CARD);

        assertEquals(1, warnings.size());
        assertEquals(WarningSeverity.HIGH, warnings.get(0).severity());
        assertTrue(warnings.get(0).message().contains("нет данных о характеристиках оборудования"));
    }

    @Test
    @DisplayName("Негативный тест: критическая ошибка в формуле (SpEL Method Not Found)")
    void evaluateCompatibility_Negative_CriticalSpelError() {
        rule.setExpression(" 'какой-то текст'.nonExistentMethod() ");
        when(ruleService.getActiveRulesByComponentType(ComponentType.POWER_SUPPLY)).thenReturn(List.of(rule));

        List<DonorWarningDto> warnings = compatibilityEngineService.evaluateCompatibility(context, ComponentType.POWER_SUPPLY);

        assertEquals(1, warnings.size());
        assertEquals(WarningSeverity.CRITICAL, warnings.get(0).severity());
        assertTrue(warnings.get(0).message().contains("Синтаксическая или логическая ошибка в формуле правила"));
    }

    @Test
    @DisplayName("Негативный тест: непредвиденная ошибка (Exception)")
    void evaluateCompatibility_Negative_UnexpectedException() {
        rule.setExpression("1 / 0 == 0");
        when(ruleService.getActiveRulesByComponentType(ComponentType.CASE)).thenReturn(List.of(rule));

        List<DonorWarningDto> warnings = compatibilityEngineService.evaluateCompatibility(context, ComponentType.CASE);

        assertEquals(1, warnings.size());
        assertEquals(WarningSeverity.CRITICAL, warnings.get(0).severity());
        assertTrue(warnings.get(0).message().contains("Внутренний системный сбой при проверке правила"));
    }
}
