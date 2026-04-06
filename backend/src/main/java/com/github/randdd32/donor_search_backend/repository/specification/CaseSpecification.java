package com.github.randdd32.donor_search_backend.repository.specification;

import com.github.randdd32.donor_search_backend.core.util.CommonSpecificationUtils;
import com.github.randdd32.donor_search_backend.core.util.QueryUtils;
import com.github.randdd32.donor_search_backend.model.hardware.CaseEntity;
import com.github.randdd32.donor_search_backend.web.dto.filter.CaseFilter;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class CaseSpecification {
    public static Specification<CaseEntity> withFilters(CaseFilter filter) {
        return (root, query, cb) -> {
            if (query != null && Long.class != query.getResultType() && long.class != query.getResultType()) {
                root.fetch("manufacturer", JoinType.LEFT);
                root.fetch("caseType", JoinType.LEFT);
                root.fetch("color", JoinType.LEFT);
                root.fetch("sidePanel", JoinType.LEFT);
            }

            List<Predicate> predicates = new ArrayList<>();

            String cleanSearch = QueryUtils.cleanSearchToken(filter.search());
            CommonSpecificationUtils.addSearchNamePredicate(predicates, root, cb, cleanSearch);

            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "manufacturer", filter.manufacturerIds());
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "caseType", filter.caseTypeIds());
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "color", filter.colorIds());
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "sidePanel", filter.sidePanelIds());

            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "lengthMm", filter.minLength(), filter.maxLength());
            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "widthMm", filter.minWidth(), filter.maxWidth());
            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "heightMm", filter.minHeight(), filter.maxHeight());

            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "int35Bays", filter.minInt35Bays(), null);
            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "expansionSlotsFullHeight", filter.minExpansionSlots(), null);

            CommonSpecificationUtils.addManyToManyFilter(predicates, root, "moboFormFactors", filter.moboFormFactorIds());
            CommonSpecificationUtils.addManyToManyFilter(predicates, root, "frontPanelUsbTypes", filter.frontPanelUsbIds());

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
