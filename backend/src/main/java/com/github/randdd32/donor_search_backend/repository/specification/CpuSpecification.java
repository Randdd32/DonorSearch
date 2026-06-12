package com.github.randdd32.donor_search_backend.repository.specification;

import com.github.randdd32.donor_search_backend.core.util.CommonSpecificationUtils;
import com.github.randdd32.donor_search_backend.core.util.QueryUtils;
import com.github.randdd32.donor_search_backend.model.hardware.CpuEntity;
import com.github.randdd32.donor_search_backend.web.dto.filter.CpuFilter;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class CpuSpecification {
    public static Specification<CpuEntity> withFilters(CpuFilter filter) {
        return (root, query, cb) -> {
            if (query != null && Long.class != query.getResultType() && long.class != query.getResultType()) {
                root.fetch("manufacturer", JoinType.LEFT);
                root.fetch("socket", JoinType.LEFT);
                root.fetch("microarchitecture", JoinType.LEFT);
                root.fetch("graphics", JoinType.LEFT);
            }

            List<Predicate> predicates = new ArrayList<>();

            String cleanSearch = QueryUtils.cleanSearchToken(filter.search());
            CommonSpecificationUtils.addSearchNamePredicate(predicates, root, cb, cleanSearch);

            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "manufacturer", filter.manufacturerIds());
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "socket", filter.socketIds());
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "microarchitecture", filter.microarchitectureIds());
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "graphics", filter.graphicsIds());

            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "coreCount", filter.minCoreCount(), filter.maxCoreCount());
            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "coreClockGhz", filter.minCoreClock(), filter.maxCoreClock());
            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "boostClockGhz", filter.minBoostClock(), filter.maxBoostClock());
            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "tdpW", filter.minTdp(), filter.maxTdp());

            CommonSpecificationUtils.addEqualityFilter(predicates, root, cb, "eccSupport", filter.eccSupport());

            if (predicates.isEmpty()) {
                return cb.conjunction();
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private CpuSpecification() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
