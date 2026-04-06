package com.github.randdd32.donor_search_backend.repository.specification;

import com.github.randdd32.donor_search_backend.core.util.CommonSpecificationUtils;
import com.github.randdd32.donor_search_backend.core.util.QueryUtils;
import com.github.randdd32.donor_search_backend.model.hardware.VideoCardEntity;
import com.github.randdd32.donor_search_backend.web.dto.filter.VideoCardFilter;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class VideoCardSpecification {
    public static Specification<VideoCardEntity> withFilters(VideoCardFilter filter) {
        return (root, query, cb) -> {
            if (query != null && Long.class != query.getResultType() && long.class != query.getResultType()) {
                root.fetch("manufacturer", JoinType.LEFT);
                root.fetch("chipset", JoinType.LEFT);
                root.fetch("memoryType", JoinType.LEFT);
                root.fetch("interfaceType", JoinType.LEFT);
                root.fetch("color", JoinType.LEFT);
            }

            List<Predicate> predicates = new ArrayList<>();

            String cleanSearch = QueryUtils.cleanSearchToken(filter.search());
            CommonSpecificationUtils.addSearchNamePredicate(predicates, root, cb, cleanSearch);

            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "manufacturer", filter.manufacturerIds());
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "chipset", filter.chipsetIds());
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "memoryType", filter.memoryTypeIds());

            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "lengthMm", filter.minLength(), filter.maxLength());
            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "tdpW", filter.minTdp(), filter.maxTdp());
            CommonSpecificationUtils.addEqualityFilter(predicates, root, cb, "slotWidth", filter.slotWidth());

            if (predicates.isEmpty()) {
                return cb.conjunction();
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private VideoCardSpecification() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
