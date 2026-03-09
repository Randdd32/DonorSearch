package com.github.randdd32.donor_search_backend.repository.specification;

import com.github.randdd32.donor_search_backend.core.util.CommonSpecificationUtils;
import com.github.randdd32.donor_search_backend.model.hardware.PowerSupplyEntity;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class PowerSupplySpecification {
    public static Specification<PowerSupplyEntity> withFilters(
            String search,
            List<Long> manufacturerIds, List<Long> typeIds,
            List<Long> efficiencyIds, List<Long> modularIds, List<Long> colorIds,
            Integer minWattage, Integer maxWattage,
            Integer minLength, Integer maxLength) {

        return (root, query, cb) -> {
            if (query != null && Long.class != query.getResultType() && long.class != query.getResultType()) {
                root.fetch("manufacturer", JoinType.LEFT);
                root.fetch("type", JoinType.LEFT);
                root.fetch("efficiency", JoinType.LEFT);
                root.fetch("modular", JoinType.LEFT);
                root.fetch("color", JoinType.LEFT);
            }

            List<Predicate> predicates = new ArrayList<>();

            CommonSpecificationUtils.addSearchNamePredicate(predicates, root, cb, search);
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "manufacturer", manufacturerIds);
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "type", typeIds);
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "efficiency", efficiencyIds);
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "modular", modularIds);
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "color", colorIds);

            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "wattageW", minWattage, maxWattage);
            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "lengthMm", minLength, maxLength);

            if (predicates.isEmpty()) {
                return cb.conjunction();
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private PowerSupplySpecification() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
