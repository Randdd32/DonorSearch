package com.github.randdd32.donor_search_backend.repository.specification;

import com.github.randdd32.donor_search_backend.core.util.CommonSpecificationUtils;
import com.github.randdd32.donor_search_backend.model.IntegrationMappingEntity;
import com.github.randdd32.donor_search_backend.model.enums.ComponentType;
import com.github.randdd32.donor_search_backend.model.enums.MappingConfidence;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public final class IntegrationMappingSpecification {
    public static Specification<IntegrationMappingEntity> withFilters(
            String search, MappingConfidence confidence, ComponentType componentType,
            Instant createdAfter, Instant createdBefore, Instant updatedAfter, Instant updatedBefore) {

        return (root, query, cb) -> {
            if (query != null && Long.class != query.getResultType() && long.class != query.getResultType()) {
                root.fetch("internalComponent", JoinType.LEFT);
            }

            List<Predicate> predicates = new ArrayList<>();

            if (search != null) {
                Predicate extName = cb.like(cb.lower(root.get("externalName")), "%" + search + "%");
                Predicate intSearchName = cb.like(cb.lower(root.get("internalComponent").get("searchName")), "%" + search + "%");
                predicates.add(cb.or(extName, intSearchName));
            }
            CommonSpecificationUtils.addEqualityFilter(predicates, root, cb, "confidence", confidence);
            if (componentType != null) {
                predicates.add(cb.equal(root.get("internalComponent").get("type"), componentType));
            }
            CommonSpecificationUtils.addAuditDateFilters(predicates, root, cb, createdAfter, createdBefore, updatedAfter, updatedBefore);

            if (predicates.isEmpty()) {
                return cb.conjunction();
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private IntegrationMappingSpecification() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
