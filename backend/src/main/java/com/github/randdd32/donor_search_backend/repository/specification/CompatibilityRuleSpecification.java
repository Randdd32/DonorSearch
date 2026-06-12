package com.github.randdd32.donor_search_backend.repository.specification;

import com.github.randdd32.donor_search_backend.core.util.CommonSpecificationUtils;
import com.github.randdd32.donor_search_backend.core.util.QueryUtils;
import com.github.randdd32.donor_search_backend.model.compatibility.CompatibilityRuleEntity;
import com.github.randdd32.donor_search_backend.web.dto.filter.CompatibilityRuleFilter;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public final class CompatibilityRuleSpecification {
    public static Specification<CompatibilityRuleEntity> withFilters(CompatibilityRuleFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            String cleanSearch = QueryUtils.cleanSearchToken(filter.search());
            if (cleanSearch != null) {
                Predicate codeMatch = cb.like(cb.lower(root.get("ruleCode")), "%" + cleanSearch + "%");
                Predicate nameMatch = cb.like(cb.lower(root.get("ruleName")), "%" + cleanSearch + "%");
                Predicate exprMatch = cb.like(cb.lower(root.get("expression")), "%" + cleanSearch + "%");
                Predicate descMatch = cb.like(cb.lower(root.get("description")), "%" + cleanSearch + "%");
                predicates.add(cb.or(codeMatch, nameMatch, exprMatch, descMatch));
            }

            CommonSpecificationUtils.addEqualityFilter(predicates, root, cb, "isActive", filter.isActive());

            if (!CollectionUtils.isEmpty(filter.targetTypes())) {
                predicates.add(root.join("targetComponentTypes").in(filter.targetTypes()));
            }

            CommonSpecificationUtils.addAuditDateFilters(predicates, root, cb, filter.createdAfter(), filter.createdBefore(),
                    filter.updatedAfter(), filter.updatedBefore());

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
