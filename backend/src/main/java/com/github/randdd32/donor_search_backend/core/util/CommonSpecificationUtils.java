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

    public static void addManyToManyFilter(List<Predicate> predicates, Root<?> root, String relationField, List<Long> ids) {
        if (!CollectionUtils.isEmpty(ids)) {
            predicates.add(root.join(relationField).get("id").in(ids));
        }
    }

    public static <Y extends Comparable<? super Y>> void addRangeFilter(List<Predicate> predicates, Root<?> root,
                                                                        CriteriaBuilder cb, String field, Y min, Y max) {
        if (min != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get(field), min));
        }
        if (max != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get(field), max));
        }
    }

    public static void addEqualityFilter(List<Predicate> predicates, Root<?> root, CriteriaBuilder cb, String field,
                                         Object value) {
        if (value != null) {
            predicates.add(cb.equal(root.get(field), value));
        }
    }

    public static void addAuditDateFilters(List<Predicate> predicates, Root<?> root, CriteriaBuilder cb,
                                           Instant createdAfter, Instant createdBefore,
                                           Instant updatedAfter, Instant updatedBefore) {
        addRangeFilter(predicates, root, cb, "createdAt", createdAfter, createdBefore);
        addRangeFilter(predicates, root, cb, "updatedAt", updatedAfter, updatedBefore);
    }

    private CommonSpecificationUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
