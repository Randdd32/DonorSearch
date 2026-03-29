package com.github.randdd32.donor_search_backend.service.compatibility;

import com.github.randdd32.donor_search_backend.core.error.HardRejectException;
import com.github.randdd32.donor_search_backend.core.error.MissingContextDataException;
import com.github.randdd32.donor_search_backend.core.util.StringUtils;
import com.github.randdd32.donor_search_backend.model.compatibility.CompatibilityRuleEntity;
import com.github.randdd32.donor_search_backend.model.enums.ComponentType;
import com.github.randdd32.donor_search_backend.service.compatibility.context.PcBuildContext;
import com.github.randdd32.donor_search_backend.web.dto.search.DonorWarningDto;
import com.github.randdd32.donor_search_backend.web.dto.search.enums.WarningSeverity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionInvocationTargetException;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompatibilityEngineService {
    private final CompatibilityRuleService ruleService;

    private final ExpressionParser parser = new SpelExpressionParser(
            new SpelParserConfiguration(SpelCompilerMode.MIXED, this.getClass().getClassLoader())
    );

    private final Map<String, Expression> expressionCache = new ConcurrentHashMap<>();

    public List<DonorWarningDto> evaluateCompatibility(PcBuildContext buildContext, ComponentType targetType) {
        List<DonorWarningDto> warnings = new ArrayList<>();

        List<CompatibilityRuleEntity> rules = ruleService.getActiveRulesByComponentType(targetType);

        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("ctx", buildContext);

        for (CompatibilityRuleEntity rule : rules) {
            try {
                Expression exp = expressionCache.computeIfAbsent(
                        rule.getExpression(),
                        parser::parseExpression
                );

                Boolean passed = exp.getValue(context, Boolean.class);

                if (Boolean.FALSE.equals(passed)) {
                    log.debug("Hard reject by rule [{}]: {}", rule.getRuleCode(), rule.getErrorMessage());
                    throw new HardRejectException(rule.getErrorMessage());
                }
            } catch (HardRejectException e) {
                throw e;
            } catch (MissingContextDataException e) {
                log.debug("Missing context data for rule [{}]: {}", rule.getRuleCode(), e.getMessage());
                warnings.add(buildMissingDataWarning(rule, StringUtils.decapitalize(e.getMessage())));
            } catch (ExpressionInvocationTargetException e) {
                if (e.getCause() instanceof MissingContextDataException missingDataEx) {
                    log.debug("Missing context data (via SpEL) for rule [{}]: {}", rule.getRuleCode(), missingDataEx.getMessage());
                    warnings.add(buildMissingDataWarning(rule, StringUtils.decapitalize(missingDataEx.getMessage())));
                } else {
                    log.error("Method execution failed in rule [{}]: {}", rule.getRuleCode(), e.getMessage());
                    warnings.add(new DonorWarningDto(
                            String.format("Внутренний системный сбой при проверке правила «%s».", rule.getRuleName()),
                            WarningSeverity.CRITICAL
                    ));
                }
            } catch (SpelEvaluationException e) {
                SpelMessage msgCode = e.getMessageCode();

                if (isMissingDataError(msgCode)) {
                    log.debug("Missing data for rule [{}]: {}", rule.getRuleCode(), e.getMessage());
                    warnings.add(buildMissingDataWarning(rule, "нет данных о характеристиках оборудования"));
                } else {
                    log.error("CRITICAL CONFIGURATION ERROR in rule [{}]: {}", rule.getRuleCode(), e.getMessage());
                    warnings.add(new DonorWarningDto(
                            String.format("Синтаксическая или логическая ошибка в формуле правила «%s». Обратитесь к администратору.",
                                    rule.getRuleName()),
                            WarningSeverity.CRITICAL
                    ));
                }
            } catch (Exception e) {
                log.error("Unexpected internal error while evaluating rule [{}]: {}", rule.getRuleCode(), e.getMessage(), e);
                warnings.add(new DonorWarningDto(
                        String.format("Внутренний системный сбой при проверке правила «%s».", rule.getRuleName()),
                        WarningSeverity.CRITICAL
                ));
            }
        }
        return warnings;
    }

    private DonorWarningDto buildMissingDataWarning(CompatibilityRuleEntity rule, String reason) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Не удалось проверить правило «%s»: %s.", rule.getRuleName(), reason));

        if (rule.getDescription() != null && !rule.getDescription().isBlank()) {
            sb.append(" Дополнительная информация о правиле: ").append(StringUtils.decapitalize(rule.getDescription()));
        }

        return new DonorWarningDto(sb.toString(), WarningSeverity.HIGH);
    }

    /**
     * Возвращает true, если исключение связано с попыткой прочитать данные,
     * которых нет (null-ссылки или пустые коллекции).
     */
    private boolean isMissingDataError(SpelMessage messageCode) {
        return messageCode == SpelMessage.PROPERTY_OR_FIELD_NOT_READABLE_ON_NULL ||
                messageCode == SpelMessage.METHOD_CALL_ON_NULL_OBJECT_NOT_ALLOWED ||
                messageCode == SpelMessage.CANNOT_INDEX_INTO_NULL_VALUE ||
                messageCode == SpelMessage.COLLECTION_INDEX_OUT_OF_BOUNDS ||
                messageCode == SpelMessage.ARRAY_INDEX_OUT_OF_BOUNDS ||
                messageCode == SpelMessage.EXCEPTION_DURING_INDEX_READ ||
                messageCode == SpelMessage.TYPE_CONVERSION_ERROR ||
                messageCode == SpelMessage.NOT_COMPARABLE ||
                messageCode == SpelMessage.OPERATOR_NOT_SUPPORTED_BETWEEN_TYPES ||
                messageCode == SpelMessage.EXCEPTION_DURING_PROPERTY_READ ||
                messageCode == SpelMessage.EXCEPTION_DURING_METHOD_INVOCATION;
    }
}
