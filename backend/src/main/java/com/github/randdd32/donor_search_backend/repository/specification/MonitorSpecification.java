package com.github.randdd32.donor_search_backend.repository.specification;

import com.github.randdd32.donor_search_backend.core.util.CommonSpecificationUtils;
import com.github.randdd32.donor_search_backend.core.util.QueryUtils;
import com.github.randdd32.donor_search_backend.model.hardware.MonitorEntity;
import com.github.randdd32.donor_search_backend.web.dto.filter.MonitorFilter;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class MonitorSpecification {
    public static Specification<MonitorEntity> withFilters(MonitorFilter filter) {
        return (root, query, cb) -> {
            if (query != null && Long.class != query.getResultType() && long.class != query.getResultType()) {
                root.fetch("manufacturer", JoinType.LEFT);
                root.fetch("resolution", JoinType.LEFT);
                root.fetch("panelType", JoinType.LEFT);
                root.fetch("aspectRatio", JoinType.LEFT);
            }

            List<Predicate> predicates = new ArrayList<>();

            String cleanSearch = QueryUtils.cleanSearchToken(filter.search());
            CommonSpecificationUtils.addSearchNamePredicate(predicates, root, cb, cleanSearch);

            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "manufacturer", filter.manufacturerIds());
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "resolution", filter.resolutionIds());
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "panelType", filter.panelTypeIds());
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "aspectRatio", filter.aspectRatioIds());

            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "screenSizeIn", filter.minScreenSize(), filter.maxScreenSize());
            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "refreshRateHz", filter.minRefreshRate(), filter.maxRefreshRate());
            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "responseTimeMs", filter.minResponseTime(), filter.maxResponseTime());

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
