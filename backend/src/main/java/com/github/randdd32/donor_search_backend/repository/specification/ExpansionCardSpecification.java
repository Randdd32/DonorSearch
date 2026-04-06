package com.github.randdd32.donor_search_backend.repository.specification;

import com.github.randdd32.donor_search_backend.core.util.CommonSpecificationUtils;
import com.github.randdd32.donor_search_backend.core.util.QueryUtils;
import com.github.randdd32.donor_search_backend.model.hardware.ExpansionCardEntity;
import com.github.randdd32.donor_search_backend.web.dto.filter.ExpansionCardFilter;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class ExpansionCardSpecification {
    public static Specification<ExpansionCardEntity> withFilters(ExpansionCardFilter filter) {
        return (root, query, cb) -> {
            if (query != null && Long.class != query.getResultType() && long.class != query.getResultType()) {
                root.fetch("manufacturer", JoinType.LEFT);
                root.fetch("interfaceType", JoinType.LEFT);
                root.fetch("color", JoinType.LEFT);
                root.fetch("audioChipset", JoinType.LEFT);
                root.fetch("protocol", JoinType.LEFT);
            }

            List<Predicate> predicates = new ArrayList<>();

            String cleanSearch = QueryUtils.cleanSearchToken(filter.search());
            CommonSpecificationUtils.addSearchNamePredicate(predicates, root, cb, cleanSearch);
            CommonSpecificationUtils.addEqualityFilter(predicates, root, cb, "cardType", filter.cardType());

            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "manufacturer", filter.manufacturerIds());
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "interfaceType", filter.interfaceIds());
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "color", filter.colorIds());
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "audioChipset", filter.audioChipsetIds());
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "protocol", filter.protocolIds());

            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "channels", filter.minChannels(), filter.maxChannels());
            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "digitalAudioBit", filter.minDigitalAudioBit(), filter.maxDigitalAudioBit());
            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "sampleRateKhz", filter.minSampleRateKhz(), filter.maxSampleRateKhz());

            if (predicates.isEmpty()) {
                return cb.conjunction();
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private ExpansionCardSpecification() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
