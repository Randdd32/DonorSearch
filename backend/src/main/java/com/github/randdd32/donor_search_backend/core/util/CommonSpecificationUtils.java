package com.github.randdd32.donor_search_backend.core.util;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.List;

public final class CommonSpecificationUtils {
    public static void addSearchNamePredicate(List<Predicate> predicates, Root<?> root, CriteriaBuilder cb, String search) {
        if (search != null) {
            predicates.add(cb.like(cb.lower(root.get("searchName")), "%" + search + "%"));
        }
    }

    public static void addDictionaryFilter(List<Predicate> predicates, Root<?> root, String relationField, List<Long> ids) {
        if (!CollectionUtils.isEmpty(ids)) {
            predicates.add(root.get(relationField).get("id").in(ids));
        }
    }

    public static void addAuditDateFilters(List<Predicate> predicates, Root<?> root, CriteriaBuilder cb,
                                           Instant createdAfter, Instant createdBefore,
                                           Instant updatedAfter, Instant updatedBefore) {
        if (createdAfter != null) predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), createdAfter));
        if (createdBefore != null) predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), createdBefore));
        if (updatedAfter != null) predicates.add(cb.greaterThanOrEqualTo(root.get("updatedAt"), updatedAfter));
        if (updatedBefore != null) predicates.add(cb.lessThanOrEqualTo(root.get("updatedAt"), updatedBefore));
    }

    private CommonSpecificationUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
