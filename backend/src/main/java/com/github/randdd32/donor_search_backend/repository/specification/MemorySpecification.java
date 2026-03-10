package com.github.randdd32.donor_search_backend.repository.specification;

import com.github.randdd32.donor_search_backend.core.util.CommonSpecificationUtils;
import com.github.randdd32.donor_search_backend.model.hardware.MemoryEntity;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class MemorySpecification {
    public static Specification<MemoryEntity> withFilters(
            String search,
            List<Long> manufacturerIds, List<Long> formFactorIds,
            List<Long> memoryTypeIds, List<Long> colorIds,
            Integer minFrequency, Integer maxFrequency,
            Integer minModulesCount, Integer maxModulesCount,
            Integer minModulesSize, Integer maxModulesSize,
            Integer minCas, Integer maxCas,
            Boolean isEcc, Boolean isRegistered) {

        return (root, query, cb) -> {
            if (query != null && Long.class != query.getResultType() && long.class != query.getResultType()) {
                root.fetch("manufacturer", JoinType.LEFT);
                root.fetch("formFactor", JoinType.LEFT);
                root.fetch("memoryType", JoinType.LEFT);
                root.fetch("color", JoinType.LEFT);
            }

            List<Predicate> predicates = new ArrayList<>();

            CommonSpecificationUtils.addSearchNamePredicate(predicates, root, cb, search);
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "manufacturer", manufacturerIds);
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "formFactor", formFactorIds);
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "memoryType", memoryTypeIds);
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "color", colorIds);

            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "frequencyMhz", minFrequency, maxFrequency);
            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "modulesCount", minModulesCount, maxModulesCount);
            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "modulesSizeGb", minModulesSize, maxModulesSize);
            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "casLatency", minCas, maxCas);

            CommonSpecificationUtils.addEqualityFilter(predicates, root, cb, "isEcc", isEcc);
            CommonSpecificationUtils.addEqualityFilter(predicates, root, cb, "isRegistered", isRegistered);

            if (predicates.isEmpty()) {
                return cb.conjunction();
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private MemorySpecification() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
