package com.github.randdd32.donor_search_backend.repository.specification;

import com.github.randdd32.donor_search_backend.core.util.CommonSpecificationUtils;
import com.github.randdd32.donor_search_backend.core.util.QueryUtils;
import com.github.randdd32.donor_search_backend.model.IntegrationMappingEntity;
import com.github.randdd32.donor_search_backend.web.dto.filter.IntegrationMappingFilter;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class IntegrationMappingSpecification {
    public static Specification<IntegrationMappingEntity> withFilters(IntegrationMappingFilter filter) {
        return (root, query, cb) -> {
            if (query != null && Long.class != query.getResultType() && long.class != query.getResultType()) {
                root.fetch("internalComponent", JoinType.LEFT);
            }

            List<Predicate> predicates = new ArrayList<>();

            String cleanSearch = QueryUtils.cleanSearchToken(filter.search());
            if (cleanSearch != null) {
                Predicate extName = cb.like(cb.lower(root.get("externalName")), "%" + cleanSearch + "%");
                Predicate intSearchName = cb.like(cb.lower(root.get("internalComponent").get("searchName")), "%" + cleanSearch + "%");
                predicates.add(cb.or(extName, intSearchName));
            }

            CommonSpecificationUtils.addEqualityFilter(predicates, root, cb, "confidence", filter.confidence());
            if (filter.componentType() != null) {
                predicates.add(cb.equal(root.get("internalComponent").get("type"), filter.componentType()));
            }
            CommonSpecificationUtils.addAuditDateFilters(predicates, root, cb, filter.createdAfter(), filter.createdBefore(),
                    filter.updatedAfter(), filter.updatedBefore());

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
