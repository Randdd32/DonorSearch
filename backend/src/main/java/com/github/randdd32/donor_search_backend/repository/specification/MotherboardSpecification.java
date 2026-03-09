package com.github.randdd32.donor_search_backend.repository.specification;

import com.github.randdd32.donor_search_backend.core.util.CommonSpecificationUtils;
import com.github.randdd32.donor_search_backend.model.hardware.MotherboardEntity;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class MotherboardSpecification {
    public static Specification<MotherboardEntity> withFilters(
            String search,
            List<Long> manufacturerIds, List<Long> socketIds,
            List<Long> formFactorIds, List<Long> memoryTypeIds,
            Integer minMaxMemoryGb, Integer minMemorySlots, Integer minMemorySpeedMhz,
            Boolean eccSupport, Boolean usesBackConnect) {

        return (root, query, cb) -> {
            if (query != null && Long.class != query.getResultType() && long.class != query.getResultType()) {
                root.fetch("manufacturer", JoinType.LEFT);
                root.fetch("socket", JoinType.LEFT);
                root.fetch("formFactor", JoinType.LEFT);
                root.fetch("memoryType", JoinType.LEFT);
                root.fetch("color", JoinType.LEFT);
            }

            List<Predicate> predicates = new ArrayList<>();

            CommonSpecificationUtils.addSearchNamePredicate(predicates, root, cb, search);
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "manufacturer", manufacturerIds);
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "socket", socketIds);
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "formFactor", formFactorIds);
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "memoryType", memoryTypeIds);

            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "maxMemoryGb", minMaxMemoryGb, null);
            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "memorySlots", minMemorySlots, null);
            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "memorySpeedMaxMhz", minMemorySpeedMhz, null);

            CommonSpecificationUtils.addEqualityFilter(predicates, root, cb, "eccSupport", eccSupport);
            CommonSpecificationUtils.addEqualityFilter(predicates, root, cb, "usesBackConnect", usesBackConnect);

            if (predicates.isEmpty()) {
                return cb.conjunction();
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private MotherboardSpecification() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
