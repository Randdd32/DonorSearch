package com.github.randdd32.donor_search_backend.repository.specification;

import com.github.randdd32.donor_search_backend.core.util.CommonSpecificationUtils;
import com.github.randdd32.donor_search_backend.core.util.QueryUtils;
import com.github.randdd32.donor_search_backend.model.hardware.PowerSupplyEntity;
import com.github.randdd32.donor_search_backend.web.dto.filter.PowerSupplyFilter;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class PowerSupplySpecification {
    public static Specification<PowerSupplyEntity> withFilters(PowerSupplyFilter filter) {
        return (root, query, cb) -> {
            if (query != null && Long.class != query.getResultType() && long.class != query.getResultType()) {
                root.fetch("manufacturer", JoinType.LEFT);
                root.fetch("powerSupplyType", JoinType.LEFT);
                root.fetch("efficiency", JoinType.LEFT);
                root.fetch("modular", JoinType.LEFT);
                root.fetch("color", JoinType.LEFT);
            }

            List<Predicate> predicates = new ArrayList<>();

            String cleanSearch = QueryUtils.cleanSearchToken(filter.search());
            CommonSpecificationUtils.addSearchNamePredicate(predicates, root, cb, cleanSearch);

            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "manufacturer", filter.manufacturerIds());
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "powerSupplyType", filter.typeIds());
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "efficiency", filter.efficiencyIds());
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "modular", filter.modularIds());
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "color", filter.colorIds());

            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "wattageW", filter.minWattage(), filter.maxWattage());
            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "lengthMm", filter.minLength(), filter.maxLength());

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
