package com.github.randdd32.donor_search_backend.repository.specification;

import com.github.randdd32.donor_search_backend.core.util.CommonSpecificationUtils;
import com.github.randdd32.donor_search_backend.core.util.QueryUtils;
import com.github.randdd32.donor_search_backend.model.hardware.CpuCoolerEntity;
import com.github.randdd32.donor_search_backend.web.dto.filter.CpuCoolerFilter;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class CpuCoolerSpecification {
    public static Specification<CpuCoolerEntity> withFilters(CpuCoolerFilter filter) {
        return (root, query, cb) -> {
            if (query != null && Long.class != query.getResultType() && long.class != query.getResultType()) {
                root.fetch("manufacturer", JoinType.LEFT);
                root.fetch("color", JoinType.LEFT);
            }

            List<Predicate> predicates = new ArrayList<>();

            String cleanSearch = QueryUtils.cleanSearchToken(filter.search());
            CommonSpecificationUtils.addSearchNamePredicate(predicates, root, cb, cleanSearch);

            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "manufacturer", filter.manufacturerIds());
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "color", filter.colorIds());
            CommonSpecificationUtils.addManyToManyFilter(predicates, root, "sockets", filter.socketIds());

            CommonSpecificationUtils.addEqualityFilter(predicates, root, cb, "isWaterCooled", filter.isWaterCooled());

            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "heightMm", filter.minHeight(), filter.maxHeight());
            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "waterCooledSizeMm", filter.minWaterSize(), filter.maxWaterSize());
            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "rpmMax", filter.minRpm(), filter.maxRpm());

            if (predicates.isEmpty()) {
                return cb.conjunction();
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private CpuCoolerSpecification() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
