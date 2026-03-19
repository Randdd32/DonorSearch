package com.github.randdd32.donor_search_backend.repository.specification;

import com.github.randdd32.donor_search_backend.core.util.CommonSpecificationUtils;
import com.github.randdd32.donor_search_backend.model.compatibility.CompatibilityRuleEntity;
import com.github.randdd32.donor_search_backend.model.enums.ComponentType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public final class CompatibilityRuleSpecification {
    public static Specification<CompatibilityRuleEntity> withFilters(
            String search,
            Boolean isActive,
            List<ComponentType> targetTypes,
            Instant createdAfter,
            Instant createdBefore,
            Instant updatedAfter,
            Instant updatedBefore) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null) {
                Predicate codeMatch = cb.like(cb.lower(root.get("ruleCode")), "%" + search + "%");
                Predicate exprMatch = cb.like(cb.lower(root.get("expression")), "%" + search + "%");
                Predicate descMatch = cb.like(cb.lower(root.get("description")), "%" + search + "%");
                predicates.add(cb.or(codeMatch, exprMatch, descMatch));
            }
            CommonSpecificationUtils.addEqualityFilter(predicates, root, cb, "isActive", isActive);
            if (!CollectionUtils.isEmpty(targetTypes)) {
                predicates.add(root.join("targetComponentTypes").in(targetTypes));
            }
            CommonSpecificationUtils.addAuditDateFilters(predicates, root, cb, createdAfter, createdBefore, updatedAfter, updatedBefore);

            if (predicates.isEmpty()) {
                return cb.conjunction();
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private CompatibilityRuleSpecification() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
