package com.github.randdd32.donor_search_backend.repository.specification;

import com.github.randdd32.donor_search_backend.core.util.CommonSpecificationUtils;
import com.github.randdd32.donor_search_backend.model.hardware.MonitorEntity;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class MonitorSpecification {
    public static Specification<MonitorEntity> withFilters(
            String search,
            List<Long> manufacturerIds, List<Long> resolutionIds,
            List<Long> panelTypeIds, List<Long> aspectRatioIds,
            Double minScreenSize, Double maxScreenSize,
            Integer minRefreshRate, Integer maxRefreshRate,
            Double minResponseTime, Double maxResponseTime) {

        return (root, query, cb) -> {
            if (query != null && Long.class != query.getResultType() && long.class != query.getResultType()) {
                root.fetch("manufacturer", JoinType.LEFT);
                root.fetch("resolution", JoinType.LEFT);
                root.fetch("panelType", JoinType.LEFT);
                root.fetch("aspectRatio", JoinType.LEFT);
            }

            List<Predicate> predicates = new ArrayList<>();

            CommonSpecificationUtils.addSearchNamePredicate(predicates, root, cb, search);
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "manufacturer", manufacturerIds);
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "resolution", resolutionIds);
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "panelType", panelTypeIds);
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "aspectRatio", aspectRatioIds);

            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "screenSizeIn", minScreenSize, maxScreenSize);
            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "refreshRateHz", minRefreshRate, maxRefreshRate);
            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "responseTimeMs", minResponseTime, maxResponseTime);

            if (predicates.isEmpty()) {
                return cb.conjunction();
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private MonitorSpecification() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
