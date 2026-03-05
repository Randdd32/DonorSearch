package com.github.randdd32.donor_search_backend.repository.specification;

import com.github.randdd32.donor_search_backend.model.CompatibilityRuleEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public final class CompatibilityRuleSpecification {
    public static Specification<CompatibilityRuleEntity> withFilters(
            String search,
            Boolean isActive,
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
            if (isActive != null) {
                predicates.add(cb.equal(root.get("isActive"), isActive));
            }
            if (createdAfter != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), createdAfter));
            }
            if (createdBefore != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), createdBefore));
            }
            if (updatedAfter != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("updatedAt"), updatedAfter));
            }
            if (updatedBefore != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("updatedAt"), updatedBefore));
            }

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
