package com.github.randdd32.donor_search_backend.service;

import com.github.randdd32.donor_search_backend.core.error.NotFoundException;
import com.github.randdd32.donor_search_backend.core.util.QueryUtils;
import com.github.randdd32.donor_search_backend.model.IntegrationMappingEntity;
import com.github.randdd32.donor_search_backend.model.enums.ComponentType;
import com.github.randdd32.donor_search_backend.model.enums.MappingConfidence;
import com.github.randdd32.donor_search_backend.model.hardware.ComponentEntity;
import com.github.randdd32.donor_search_backend.repository.IntegrationMappingRepository;
import com.github.randdd32.donor_search_backend.repository.hardware.ComponentRepository;
import com.github.randdd32.donor_search_backend.repository.specification.IntegrationMappingSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class IntegrationMappingService extends AbstractCrudService<IntegrationMappingEntity, IntegrationMappingRepository> {
    private final ComponentRepository componentRepository;

    public IntegrationMappingService(IntegrationMappingRepository repository, ComponentRepository componentRepository) {
        super(repository, IntegrationMappingEntity.class);
        this.componentRepository = componentRepository;
    }

    @Transactional(readOnly = true)
    public Optional<IntegrationMappingEntity> findByExternalName(String externalName) {
        if (externalName == null || externalName.isBlank()) {
            return Optional.empty();
        }
        return repository.findByExternalNameIgnoreCase(externalName.trim());
    }

    @Transactional(readOnly = true)
    public Page<IntegrationMappingEntity> getAll(
            String search, MappingConfidence confidence, ComponentType componentType,
            Instant createdAfter, Instant createdBefore, Instant updatedAfter, Instant updatedBefore,
            Pageable pageable) {

        Specification<IntegrationMappingEntity> spec = IntegrationMappingSpecification.withFilters(
                QueryUtils.cleanSearchToken(search), confidence, componentType,
                createdAfter, createdBefore, updatedAfter, updatedBefore
        );
        return repository.findAll(spec, pageable);
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getMappedComponentIds(List<String> externalNames) {
        if (CollectionUtils.isEmpty(externalNames)) {
            return java.util.Collections.emptyMap();
        }

        List<String> lowerNames = externalNames.stream()
                .filter(name -> name != null && !name.isBlank())
                .map(String::toLowerCase)
                .toList();
        if (lowerNames.isEmpty()) {
            return java.util.Collections.emptyMap();
        }

        List<Object[]> results = repository.findMappedIdsByNames(lowerNames);
        return results.stream().collect(Collectors.toMap(
                row -> ((String) row[0]).toLowerCase(),
                row -> (Long) row[1],
                (id1, id2) -> id1
        ));
    }

    @Transactional
    public IntegrationMappingEntity createFromDto(IntegrationMappingEntity entity, Long internalComponentId) {
        validate(entity, null);
        ComponentEntity component = componentRepository.findById(internalComponentId)
                .orElseThrow(() -> new NotFoundException(ComponentEntity.class, internalComponentId));
        entity.setInternalComponent(component);
        return repository.save(entity);
    }

    @Override
    protected void updateFields(IntegrationMappingEntity existing, IntegrationMappingEntity updated) {
        existing.setExternalName(updated.getExternalName());
        existing.setConfidence(updated.getConfidence());
    }

    @Transactional
    public IntegrationMappingEntity updateInternalComponent(Long mappingId, Long newComponentId, MappingConfidence newConfidence) {
        IntegrationMappingEntity mapping = getById(mappingId);
        ComponentEntity component = componentRepository.findById(newComponentId)
                .orElseThrow(() -> new NotFoundException(ComponentEntity.class, newComponentId));
        mapping.setInternalComponent(component);
        mapping.setConfidence(newConfidence);
        return repository.save(mapping);
    }

    @Override
    protected void validate(IntegrationMappingEntity entity, Long id) {
        if (entity == null) throw new IllegalArgumentException("IntegrationMapping entity is null");

        validateStringField(entity.getExternalName(), "External Name");
        if (entity.getConfidence() == null) throw new IllegalArgumentException("Confidence level must not be null");

        Optional<IntegrationMappingEntity> existing = repository.findByExternalNameIgnoreCase(entity.getExternalName());
        if (existing.isPresent() && !existing.get().getId().equals(id)) {
            throw new IllegalArgumentException("Mapping for external name '" + entity.getExternalName() + "' already exists");
        }
    }
}
