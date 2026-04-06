package com.github.randdd32.donor_search_backend.repository.specification;

import com.github.randdd32.donor_search_backend.core.util.CommonSpecificationUtils;
import com.github.randdd32.donor_search_backend.core.util.QueryUtils;
import com.github.randdd32.donor_search_backend.model.hardware.MemoryEntity;
import com.github.randdd32.donor_search_backend.web.dto.filter.MemoryFilter;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class MemorySpecification {
    public static Specification<MemoryEntity> withFilters(MemoryFilter filter) {
        return (root, query, cb) -> {
            if (query != null && Long.class != query.getResultType() && long.class != query.getResultType()) {
                root.fetch("manufacturer", JoinType.LEFT);
                root.fetch("formFactor", JoinType.LEFT);
                root.fetch("memoryType", JoinType.LEFT);
                root.fetch("color", JoinType.LEFT);
            }

            List<Predicate> predicates = new ArrayList<>();

            String cleanSearch = QueryUtils.cleanSearchToken(filter.search());
            CommonSpecificationUtils.addSearchNamePredicate(predicates, root, cb, cleanSearch);

            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "manufacturer", filter.manufacturerIds());
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "formFactor", filter.formFactorIds());
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "memoryType", filter.memoryTypeIds());
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "color", filter.colorIds());

            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "frequencyMhz", filter.minFrequency(), filter.maxFrequency());
            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "modulesCount", filter.minModulesCount(), filter.maxModulesCount());
            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "modulesSizeGb", filter.minModulesSize(), filter.maxModulesSize());
            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "casLatency", filter.minCas(), filter.maxCas());

            CommonSpecificationUtils.addEqualityFilter(predicates, root, cb, "isEcc", filter.isEcc());
            CommonSpecificationUtils.addEqualityFilter(predicates, root, cb, "isRegistered", filter.isRegistered());

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
