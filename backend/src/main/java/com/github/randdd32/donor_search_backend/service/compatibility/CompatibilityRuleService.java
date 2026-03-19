package com.github.randdd32.donor_search_backend.service.compatibility;

import com.github.randdd32.donor_search_backend.core.util.QueryUtils;
import com.github.randdd32.donor_search_backend.model.compatibility.CompatibilityRuleEntity;
import com.github.randdd32.donor_search_backend.model.enums.ComponentType;
import com.github.randdd32.donor_search_backend.repository.compatibility.CompatibilityRuleRepository;
import com.github.randdd32.donor_search_backend.repository.specification.CompatibilityRuleSpecification;
import com.github.randdd32.donor_search_backend.service.AbstractCrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class CompatibilityRuleService extends AbstractCrudService<CompatibilityRuleEntity, CompatibilityRuleRepository> {
    public CompatibilityRuleService(CompatibilityRuleRepository repository) {
        super(repository, CompatibilityRuleEntity.class);
    }

    @Transactional(readOnly = true)
    public Page<CompatibilityRuleEntity> getAll(
            String search, Boolean isActive, List<ComponentType> targetTypes,
            Instant createdAfter, Instant createdBefore,
            Instant updatedAfter, Instant updatedBefore,
            Pageable pageable) {

        Specification<CompatibilityRuleEntity> spec = CompatibilityRuleSpecification.withFilters(
                QueryUtils.cleanSearchToken(search), isActive, targetTypes,
                createdAfter, createdBefore, updatedAfter, updatedBefore
        );
        return repository.findAll(spec, pageable);
    }

    @Transactional(readOnly = true)
    public List<CompatibilityRuleEntity> getActiveRulesByComponentType(ComponentType type) {
        Specification<CompatibilityRuleEntity> spec = CompatibilityRuleSpecification.withFilters(
                null, true, List.of(type), null, null, null, null
        );
        return repository.findAll(spec);
    }

    @Override
    protected void updateFields(CompatibilityRuleEntity existing, CompatibilityRuleEntity updated) {
        existing.setRuleCode(updated.getRuleCode());
        existing.setExpression(updated.getExpression());
        existing.setErrorMessage(updated.getErrorMessage());
        existing.setIsActive(updated.getIsActive());
        existing.setDescription(updated.getDescription());

        existing.getTargetComponentTypes().clear();
        existing.getTargetComponentTypes().addAll(updated.getTargetComponentTypes());
    }

    @Override
    protected void validate(CompatibilityRuleEntity entity, Long id) {
        if (entity == null) throw new IllegalArgumentException("CompatibilityRule entity is null");

        validateStringField(entity.getRuleCode(), "Rule code");
        validateStringField(entity.getExpression(), "Expression (SpEL)");
        validateStringField(entity.getErrorMessage(), "Error message");
        if (CollectionUtils.isEmpty(entity.getTargetComponentTypes())) {
            throw new IllegalArgumentException("Target component types must not be empty");
        }

        Optional<CompatibilityRuleEntity> existing = repository.findByRuleCodeIgnoreCase(entity.getRuleCode());
        if (existing.isPresent() && !existing.get().getId().equals(id)) {
            throw new IllegalArgumentException(
                    String.format("Compatibility rule with code '%s' already exists", entity.getRuleCode())
            );
        }
    }
}
