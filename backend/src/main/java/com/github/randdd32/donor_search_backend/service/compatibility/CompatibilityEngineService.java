package com.github.randdd32.donor_search_backend.service.compatibility;

import com.github.randdd32.donor_search_backend.core.error.HardRejectException;
import com.github.randdd32.donor_search_backend.model.compatibility.CompatibilityRuleEntity;
import com.github.randdd32.donor_search_backend.model.enums.ComponentType;
import com.github.randdd32.donor_search_backend.web.dto.compatibility.PcBuildContext;
import com.github.randdd32.donor_search_backend.web.dto.search.DonorWarningDto;
import com.github.randdd32.donor_search_backend.web.dto.search.enums.WarningSeverity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompatibilityEngineService {
    private final CompatibilityRuleService ruleService;
    private final ExpressionParser parser = new SpelExpressionParser();

    public List<DonorWarningDto> evaluateCompatibility(PcBuildContext buildContext, ComponentType targetType) {
        List<DonorWarningDto> warnings = new ArrayList<>();

        List<CompatibilityRuleEntity> rules = ruleService.getActiveRulesByComponentType(targetType);

        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("ctx", buildContext);

        for (CompatibilityRuleEntity rule : rules) {
            try {
                Expression exp = parser.parseExpression(rule.getExpression());
                Boolean passed = exp.getValue(context, Boolean.class);

                if (Boolean.FALSE.equals(passed)) {
                    log.debug("Hard reject by rule [{}]: {}", rule.getRuleCode(), rule.getErrorMessage());
                    throw new HardRejectException(rule.getErrorMessage());
                }
            } catch (SpelEvaluationException e) {
                log.debug("Missing data for rule [{}]: {}", rule.getRuleCode(), e.getMessage());
                warnings.add(new DonorWarningDto(
                        "Не удалось проверить правило[" + rule.getRuleCode() + "] из-за нехватки" +
                                "информации о характеристиках. " + rule.getDescription(),
                        WarningSeverity.HIGH
                ));
            } catch (Exception e) {
                log.warn("Error parsing rule [{}]: {}", rule.getRuleCode(), e.getMessage());
                warnings.add(new DonorWarningDto(
                        "Внутренний сбой при проверке правила [" + rule.getRuleCode() + "].",
                        WarningSeverity.HIGH
                ));
            }
        }
        return warnings;
    }
}
