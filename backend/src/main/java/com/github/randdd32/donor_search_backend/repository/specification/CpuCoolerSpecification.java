package com.github.randdd32.donor_search_backend.repository.specification;

import com.github.randdd32.donor_search_backend.core.util.CommonSpecificationUtils;
import com.github.randdd32.donor_search_backend.model.hardware.CpuCoolerEntity;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class CpuCoolerSpecification {
    public static Specification<CpuCoolerEntity> withFilters(
            String search,
            List<Long> manufacturerIds, List<Long> colorIds, List<Long> socketIds,
            Boolean isWaterCooled,
            Integer minHeight, Integer maxHeight,
            Integer minWaterSize, Integer maxWaterSize,
            Integer minRpm, Integer maxRpm) {

        return (root, query, cb) -> {
            if (query != null && Long.class != query.getResultType() && long.class != query.getResultType()) {
                root.fetch("manufacturer", JoinType.LEFT);
                root.fetch("color", JoinType.LEFT);
            }

            List<Predicate> predicates = new ArrayList<>();

            CommonSpecificationUtils.addSearchNamePredicate(predicates, root, cb, search);
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "manufacturer", manufacturerIds);
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "color", colorIds);
            CommonSpecificationUtils.addManyToManyFilter(predicates, root, "sockets", socketIds);

            CommonSpecificationUtils.addEqualityFilter(predicates, root, cb, "isWaterCooled", isWaterCooled);

            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "heightMm", minHeight, maxHeight);
            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "waterCooledSizeMm", minWaterSize, maxWaterSize);
            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "rpmMax", minRpm, maxRpm);

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
