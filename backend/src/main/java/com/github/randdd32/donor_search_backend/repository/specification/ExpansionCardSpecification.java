package com.github.randdd32.donor_search_backend.repository.specification;

import com.github.randdd32.donor_search_backend.core.util.CommonSpecificationUtils;
import com.github.randdd32.donor_search_backend.model.enums.ExpansionCardType;
import com.github.randdd32.donor_search_backend.model.hardware.ExpansionCardEntity;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class ExpansionCardSpecification {
    public static Specification<ExpansionCardEntity> withFilters(
            String search, ExpansionCardType cardType,
            List<Long> manufacturerIds, List<Long> interfaceIds, List<Long> colorIds,
            List<Long> audioChipsetIds, List<Long> protocolIds,
            Double minChannels, Double maxChannels,
            Integer minDigitalAudioBit, Integer maxDigitalAudioBit,
            Double minSampleRateKhz, Double maxSampleRateKhz) {

        return (root, query, cb) -> {
            if (query != null && Long.class != query.getResultType() && long.class != query.getResultType()) {
                root.fetch("manufacturer", JoinType.LEFT);
                root.fetch("interfaceType", JoinType.LEFT);
                root.fetch("color", JoinType.LEFT);
                root.fetch("audioChipset", JoinType.LEFT);
                root.fetch("protocol", JoinType.LEFT);
            }

            List<Predicate> predicates = new ArrayList<>();

            CommonSpecificationUtils.addSearchNamePredicate(predicates, root, cb, search);
            CommonSpecificationUtils.addEqualityFilter(predicates, root, cb, "cardType", cardType);

            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "manufacturer", manufacturerIds);
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "interfaceType", interfaceIds);
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "color", colorIds);
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "audioChipset", audioChipsetIds);
            CommonSpecificationUtils.addDictionaryFilter(predicates, root, "protocol", protocolIds);

            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "channels", minChannels, maxChannels);
            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "digitalAudioBit", minDigitalAudioBit, maxDigitalAudioBit);
            CommonSpecificationUtils.addRangeFilter(predicates, root, cb, "sampleRateKhz", minSampleRateKhz, maxSampleRateKhz);

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
