package com.github.randdd32.donor_search_backend.repository.specification;

import com.github.randdd32.donor_search_backend.core.util.CommonSpecificationUtils;
import com.github.randdd32.donor_search_backend.model.hardware.VideoCardEntity;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class VideoCardSpecification {
    public static Specification<VideoCardEntity> withFilters(
            String search,
            List<Long> manufacturerIds,
            List<Long> chipsetIds,
            List<Long> memoryTypeIds,
            Integer minLength, Integer maxLength,
            Integer minTdp, Integer maxTdp,
            Integer slotWidth) {

        return (root, query, cb) -> {
            if (query != null && Long.class != query.getResultType() && long.class != query.getResultType()) {
                root.fetch("manufacturer", JoinType.LEFT);
                root.fetch("chipset", JoinType.LEFT);
                root.fetch("memoryType", JoinType.LEFT);
                root.fetch("interfaceType", JoinType.LEFT);
                root.fetch("color", JoinType.LEFT);
            }

            List<Predicate> predicates = new ArrayList<>();

            CommonSpecificationUtils.addSearchNamePredicate(predicates, root, cb, search);
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "manufacturer", manufacturerIds);
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "chipset", chipsetIds);
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "memoryType", memoryTypeIds);

            if (minLength != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("lengthMm"), minLength));
            }
            if (maxLength != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("lengthMm"), maxLength));
            }
            if (minTdp != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("tdpW"), minTdp));
            }
            if (maxTdp != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("tdpW"), maxTdp));
            }
            if (slotWidth != null) {
                predicates.add(cb.equal(root.get("slotWidth"), slotWidth));
            }

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
