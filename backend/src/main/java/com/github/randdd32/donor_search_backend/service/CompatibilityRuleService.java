package com.github.randdd32.donor_search_backend.service;

import com.github.randdd32.donor_search_backend.core.error.NotFoundException;
import com.github.randdd32.donor_search_backend.core.util.QueryUtils;
import com.github.randdd32.donor_search_backend.model.CompatibilityRuleEntity;
import com.github.randdd32.donor_search_backend.repository.CompatibilityRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompatibilityRuleService extends AbstractValidatingService<CompatibilityRuleEntity> {
    private final CompatibilityRuleRepository repository;

    @Transactional(readOnly = true)
    public Page<CompatibilityRuleEntity> getAll(
            String search,
            Boolean isActive,
            Instant createdAfter,
            Instant createdBefore,
            Instant updatedAfter,
            Instant updatedBefore,
            List<String> sort,
            int page,
            int size) {
        Sort appliedSort = QueryUtils.createSort(sort, Sort.by(Sort.Direction.DESC, "createdAt"));
        Pageable pageRequest = PageRequest.of(page, size, appliedSort);

        return repository.findByFilters(
                QueryUtils.cleanSearchToken(search),
                isActive,
                createdAfter,
                createdBefore,
                updatedAfter,
                updatedBefore,
                pageRequest
        );
    }

    @Transactional(readOnly = true)
    public CompatibilityRuleEntity getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(CompatibilityRuleEntity.class, id));
    }

    @Transactional
    public CompatibilityRuleEntity create(CompatibilityRuleEntity entity) {
        validate(entity, null);
        return repository.save(entity);
    }

    @Transactional
    public CompatibilityRuleEntity update(Long id, CompatibilityRuleEntity updatedEntity) {
        validate(updatedEntity, id);
        CompatibilityRuleEntity existing = getById(id);

        existing.setRuleCode(updatedEntity.getRuleCode());
        existing.setExpression(updatedEntity.getExpression());
        existing.setErrorMessage(updatedEntity.getErrorMessage());
        existing.setIsActive(updatedEntity.getIsActive());
        existing.setDescription(updatedEntity.getDescription());

        return repository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(getById(id));
    }

    @Override
    protected void validate(CompatibilityRuleEntity entity, Long id) {
        if (entity == null) {
            throw new IllegalArgumentException("CompatibilityRule entity is null");
        }

        validateStringField(entity.getRuleCode(), "Rule code");
        validateStringField(entity.getExpression(), "Expression (SpEL)");
        validateStringField(entity.getErrorMessage(), "Error message");

        Optional<CompatibilityRuleEntity> existing = repository.findByRuleCodeIgnoreCase(entity.getRuleCode());
        if (existing.isPresent() && !existing.get().getId().equals(id)) {
            throw new IllegalArgumentException(
                    String.format("Compatibility rule with code '%s' already exists", entity.getRuleCode())
            );
        }
    }
}
