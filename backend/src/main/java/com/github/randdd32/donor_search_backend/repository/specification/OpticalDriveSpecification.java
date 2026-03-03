package com.github.randdd32.donor_search_backend.repository.specification;

import com.github.randdd32.donor_search_backend.model.hardware.OpticalDriveEntity;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class OpticalDriveSpecification {
    public static Specification<OpticalDriveEntity> withFilters(
            String search,
            List<Long> manufacturerIds,
            List<Long> formFactorIds,
            List<Long> interfaceIds) {

        return (root, query, cb) -> {
            if (Long.class != query.getResultType() && long.class != query.getResultType()) {
                root.fetch("manufacturer", JoinType.LEFT);
                root.fetch("formFactor", JoinType.LEFT);
                root.fetch("storageInterface", JoinType.LEFT);
            }

            List<Predicate> predicates = new ArrayList<>();

            if (search != null) {
                predicates.add(cb.like(cb.lower(root.get("searchName")), "%" + search + "%"));
            }
            if (manufacturerIds != null && !manufacturerIds.isEmpty()) {
                predicates.add(root.get("manufacturer").get("id").in(manufacturerIds));
            }
            if (formFactorIds != null && !formFactorIds.isEmpty()) {
                predicates.add(root.get("formFactor").get("id").in(formFactorIds));
            }
            if (interfaceIds != null && !interfaceIds.isEmpty()) {
                predicates.add(root.get("storageInterface").get("id").in(interfaceIds));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
