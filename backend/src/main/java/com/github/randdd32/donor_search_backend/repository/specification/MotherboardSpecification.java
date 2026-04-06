package com.github.randdd32.donor_search_backend.repository.specification;

import com.github.randdd32.donor_search_backend.core.util.CommonSpecificationUtils;
import com.github.randdd32.donor_search_backend.core.util.QueryUtils;
import com.github.randdd32.donor_search_backend.model.hardware.MotherboardEntity;
import com.github.randdd32.donor_search_backend.web.dto.filter.MotherboardFilter;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class MotherboardSpecification {
    public static Specification<MotherboardEntity> withFilters(MotherboardFilter filter) {
        return (root, query, cb) -> {
            if (query != null && Long.class != query.getResultType() && long.class != query.getResultType()) {
                root.fetch("manufacturer", JoinType.LEFT);
                root.fetch("socket", JoinType.LEFT);
                root.fetch("formFactor", JoinType.LEFT);
                root.fetch("memoryType", JoinType.LEFT);
                root.fetch("color", JoinType.LEFT);
            }

            List<Predicate> predicates = new ArrayList<>();

            String cleanSearch = QueryUtils.cleanSearchToken(filter.search());
            CommonSpecificationUtils.addSearchNamePredicate(predicates, root, cb, cleanSearch);

            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "manufacturer", filter.manufacturerIds());
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "socket", filter.socketIds());
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "formFactor", filter.formFactorIds());
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "memoryType", filter.memoryTypeIds());

            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "maxMemoryGb", filter.minMaxMemoryGb(), null);
            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "memorySlots", filter.minMemorySlots(), null);
            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "memorySpeedMaxMhz", filter.minMemorySpeedMhz(), null);

            CommonSpecificationUtils.addEqualityFilter(predicates, root, cb, "eccSupport", filter.eccSupport());
            CommonSpecificationUtils.addEqualityFilter(predicates, root, cb, "usesBackConnect", filter.usesBackConnect());

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
