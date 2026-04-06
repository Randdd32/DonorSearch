package com.github.randdd32.donor_search_backend.repository.specification;

import com.github.randdd32.donor_search_backend.core.util.CommonSpecificationUtils;
import com.github.randdd32.donor_search_backend.core.util.QueryUtils;
import com.github.randdd32.donor_search_backend.model.hardware.StorageEntity;
import com.github.randdd32.donor_search_backend.web.dto.filter.StorageFilter;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class StorageSpecification {
    public static Specification<StorageEntity> withFilters(StorageFilter filter) {
        return (root, query, cb) -> {
            if (query != null && Long.class != query.getResultType() && long.class != query.getResultType()) {
                root.fetch("manufacturer", JoinType.LEFT);
                root.fetch("storageType", JoinType.LEFT);
                root.fetch("formFactor", JoinType.LEFT);
                root.fetch("color", JoinType.LEFT);
            }

            List<Predicate> predicates = new ArrayList<>();

            String cleanSearch = QueryUtils.cleanSearchToken(filter.search());
            CommonSpecificationUtils.addSearchNamePredicate(predicates, root, cb, cleanSearch);

            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "manufacturer", filter.manufacturerIds());
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "storageType", filter.typeIds());
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "formFactor", filter.formFactorIds());
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "color", filter.colorIds());
            CommonSpecificationUtils.addManyToManyFilter(predicates, root, "interfaces", filter.interfaceIds());

            CommonSpecificationUtils.addEqualityFilter(predicates, root, cb, "isExternal", filter.isExternal());

            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "capacityGb", filter.minCapacity(), filter.maxCapacity());
            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "cacheMb", filter.minCache(), filter.maxCache());
            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "rpm", filter.minRpm(), filter.maxRpm());

            if (predicates.isEmpty()) {
                return cb.conjunction();
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private StorageSpecification() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
