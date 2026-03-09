package com.github.randdd32.donor_search_backend.repository.specification;

import com.github.randdd32.donor_search_backend.core.util.CommonSpecificationUtils;
import com.github.randdd32.donor_search_backend.model.hardware.CaseEntity;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class CaseSpecification {
    public static Specification<CaseEntity> withFilters(
            String search,
            List<Long> manufacturerIds, List<Long> caseTypeIds,
            List<Long> colorIds, List<Long> sidePanelIds,
            Integer minLength, Integer maxLength,
            Integer minWidth, Integer maxWidth,
            Integer minHeight, Integer maxHeight,
            Integer minInt35Bays, Integer minExpansionSlots,
            List<Long> moboFormFactorIds,
            List<Long> frontPanelUsbIds) {

        return (root, query, cb) -> {
            if (query != null && Long.class != query.getResultType() && long.class != query.getResultType()) {
                root.fetch("manufacturer", JoinType.LEFT);
                root.fetch("caseType", JoinType.LEFT);
                root.fetch("color", JoinType.LEFT);
                root.fetch("sidePanel", JoinType.LEFT);
            }

            List<Predicate> predicates = new ArrayList<>();

            CommonSpecificationUtils.addSearchNamePredicate(predicates, root, cb, search);
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "manufacturer", manufacturerIds);
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "caseType", caseTypeIds);
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "color", colorIds);
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "sidePanel", sidePanelIds);

            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "lengthMm", minLength, maxLength);
            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "widthMm", minWidth, maxWidth);
            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "heightMm", minHeight, maxHeight);

            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "int35Bays", minInt35Bays, null);
            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "expansionSlotsFullHeight", minExpansionSlots, null);

            CommonSpecificationUtils.addManyToManyFilter(predicates, root, "moboFormFactors", moboFormFactorIds);
            CommonSpecificationUtils.addManyToManyFilter(predicates, root, "frontPanelUsbTypes", frontPanelUsbIds);

            if (predicates.isEmpty()) {
                return cb.conjunction();
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private CaseSpecification() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
