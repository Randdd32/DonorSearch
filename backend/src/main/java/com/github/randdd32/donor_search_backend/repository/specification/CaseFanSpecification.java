package com.github.randdd32.donor_search_backend.repository.specification;

import com.github.randdd32.donor_search_backend.core.util.CommonSpecificationUtils;
import com.github.randdd32.donor_search_backend.core.util.QueryUtils;
import com.github.randdd32.donor_search_backend.model.hardware.CaseFanEntity;
import com.github.randdd32.donor_search_backend.web.dto.filter.CaseFanFilter;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class CaseFanSpecification {
    public static Specification<CaseFanEntity> withFilters(CaseFanFilter filter) {
        return (root, query, cb) -> {
            if (query != null && Long.class != query.getResultType() && long.class != query.getResultType()) {
                root.fetch("manufacturer", JoinType.LEFT);
                root.fetch("color", JoinType.LEFT);
            }

            List<Predicate> predicates = new ArrayList<>();

            String cleanSearch = QueryUtils.cleanSearchToken(filter.search());
            CommonSpecificationUtils.addSearchNamePredicate(predicates, root, cb, cleanSearch);

            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "manufacturer", filter.manufacturerIds());
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "color", filter.colorIds());

            CommonSpecificationUtils.addManyToManyFilter(predicates, root, "connectors", filter.connectorIds());
            CommonSpecificationUtils.addInFilter(predicates, root, "sizeMm", filter.sizes());
            CommonSpecificationUtils.addEqualityFilter(predicates, root, cb, "pwm", filter.pwm());

            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "rpmMax", filter.minRpm(), filter.maxRpm());
            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "airflowMax", filter.minAirflow(), filter.maxAirflow());

            if (predicates.isEmpty()) {
                return cb.conjunction();
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private CaseFanSpecification() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
