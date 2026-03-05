package com.github.randdd32.donor_search_backend.repository.specification;

import com.github.randdd32.donor_search_backend.model.hardware.OpticalDriveEntity;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public final class OpticalDriveSpecification {
    public static Specification<OpticalDriveEntity> withFilters(
            String search,
            List<Long> manufacturerIds,
            List<Long> formFactorIds,
            List<Long> interfaceIds) {

        return (root, query, cb) -> {
            if (query != null && Long.class != query.getResultType() && long.class != query.getResultType()) {
                root.fetch("manufacturer", JoinType.LEFT);
                root.fetch("formFactor", JoinType.LEFT);
                root.fetch("storageInterface", JoinType.LEFT);
            }

            List<Predicate> predicates = new ArrayList<>();

            if (search != null) {
                predicates.add(cb.like(cb.lower(root.get("searchName")), "%" + search + "%"));
            }
            if (!CollectionUtils.isEmpty(manufacturerIds)) {
                predicates.add(root.get("manufacturer").get("id").in(manufacturerIds));
            }
            if (!CollectionUtils.isEmpty(formFactorIds)) {
                predicates.add(root.get("formFactor").get("id").in(formFactorIds));
            }
            if (!CollectionUtils.isEmpty(interfaceIds)) {
                predicates.add(root.get("storageInterface").get("id").in(interfaceIds));
            }

            if (predicates.isEmpty()) {
                return cb.conjunction();
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private OpticalDriveSpecification() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
