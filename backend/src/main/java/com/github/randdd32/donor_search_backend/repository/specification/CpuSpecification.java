package com.github.randdd32.donor_search_backend.repository.specification;

import com.github.randdd32.donor_search_backend.core.util.CommonSpecificationUtils;
import com.github.randdd32.donor_search_backend.model.hardware.CpuEntity;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class CpuSpecification {
    public static Specification<CpuEntity> withFilters(
            String search,
            List<Long> manufacturerIds, List<Long> socketIds,
            List<Long> microarchitectureIds, List<Long> graphicsIds,
            Integer minCoreCount, Integer maxCoreCount,
            Double minCoreClock, Double maxCoreClock,
            Double minBoostClock, Double maxBoostClock,
            Integer minTdp, Integer maxTdp,
            Boolean eccSupport) {

        return (root, query, cb) -> {
            if (query != null && Long.class != query.getResultType() && long.class != query.getResultType()) {
                root.fetch("manufacturer", JoinType.LEFT);
                root.fetch("socket", JoinType.LEFT);
                root.fetch("microarchitecture", JoinType.LEFT);
                root.fetch("graphics", JoinType.LEFT);
            }

            List<Predicate> predicates = new ArrayList<>();

            CommonSpecificationUtils.addSearchNamePredicate(predicates, root, cb, search);
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "manufacturer", manufacturerIds);
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "socket", socketIds);
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "microarchitecture", microarchitectureIds);
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "graphics", graphicsIds);

            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "coreCount", minCoreCount, maxCoreCount);
            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "coreClockGhz", minCoreClock, maxCoreClock);
            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "boostClockGhz", minBoostClock, maxBoostClock);
            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "tdpW", minTdp, maxTdp);

            CommonSpecificationUtils.addEqualityFilter(predicates, root, cb, "eccSupport", eccSupport);

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
