package com.github.randdd32.donor_search_backend.repository.specification;

import com.github.randdd32.donor_search_backend.core.util.CommonSpecificationUtils;
import com.github.randdd32.donor_search_backend.model.hardware.StorageEntity;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class StorageSpecification {
    public static Specification<StorageEntity> withFilters(
            String search,
            List<Long> manufacturerIds, List<Long> typeIds,
            List<Long> formFactorIds, List<Long> colorIds, List<Long> interfaceIds,
            Boolean isExternal,
            Integer minCapacity, Integer maxCapacity,
            Integer minCache, Integer maxCache,
            Integer minRpm, Integer maxRpm) {

        return (root, query, cb) -> {
            if (query != null && Long.class != query.getResultType() && long.class != query.getResultType()) {
                root.fetch("manufacturer", JoinType.LEFT);
                root.fetch("type", JoinType.LEFT);
                root.fetch("formFactor", JoinType.LEFT);
                root.fetch("color", JoinType.LEFT);
            }

            List<Predicate> predicates = new ArrayList<>();

            CommonSpecificationUtils.addSearchNamePredicate(predicates, root, cb, search);
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "manufacturer", manufacturerIds);
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "type", typeIds);
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "formFactor", formFactorIds);
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "color", colorIds);
            CommonSpecificationUtils.addManyToManyFilter(predicates, root, "interfaces", interfaceIds);

            CommonSpecificationUtils.addEqualityFilter(predicates, root, cb, "isExternal", isExternal);

            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "capacityGb", minCapacity, maxCapacity);
            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "cacheMb", minCache, maxCache);
            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "rpm", minRpm, maxRpm);

            if (predicates.isEmpty()) {
                return cb.conjunction();
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private StorageSpecification() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
