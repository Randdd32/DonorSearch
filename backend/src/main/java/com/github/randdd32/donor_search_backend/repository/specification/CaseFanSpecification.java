package com.github.randdd32.donor_search_backend.repository.specification;

import com.github.randdd32.donor_search_backend.core.util.CommonSpecificationUtils;
import com.github.randdd32.donor_search_backend.model.hardware.CaseFanEntity;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class CaseFanSpecification {
    public static Specification<CaseFanEntity> withFilters(
            String search,
            List<Long> manufacturerIds, List<Long> colorIds, List<Long> connectorIds,
            List<Integer> sizes, Boolean pwm,
            Integer minRpm, Integer maxRpm,
            Integer minAirflow, Integer maxAirflow) {

        return (root, query, cb) -> {
            if (query != null && Long.class != query.getResultType() && long.class != query.getResultType()) {
                root.fetch("manufacturer", JoinType.LEFT);
                root.fetch("color", JoinType.LEFT);
            }

            List<Predicate> predicates = new ArrayList<>();

            CommonSpecificationUtils.addSearchNamePredicate(predicates, root, cb, search);
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "manufacturer", manufacturerIds);
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "color", colorIds);

            CommonSpecificationUtils.addManyToManyFilter(predicates, root, "connectors", connectorIds);
            CommonSpecificationUtils.addInFilter(predicates, root, "sizeMm", sizes);
            CommonSpecificationUtils.addEqualityFilter(predicates, root, cb, "pwm", pwm);

            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "rpmMax", minRpm, maxRpm);
            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "airflowMax", minAirflow, maxAirflow);

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
