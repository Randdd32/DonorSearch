package com.github.randdd32.donor_search_backend.service.hardware;

import com.github.randdd32.donor_search_backend.core.util.QueryUtils;
import com.github.randdd32.donor_search_backend.model.enums.ExpansionCardType;
import com.github.randdd32.donor_search_backend.model.hardware.ExpansionCardEntity;
import com.github.randdd32.donor_search_backend.repository.hardware.ExpansionCardRepository;
import com.github.randdd32.donor_search_backend.repository.specification.ExpansionCardSpecification;
import com.github.randdd32.donor_search_backend.service.AbstractReadService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExpansionCardService extends AbstractReadService<ExpansionCardEntity, ExpansionCardRepository> {
    public ExpansionCardService(ExpansionCardRepository repository) {
        super(repository, ExpansionCardEntity.class);
    }

    @Transactional(readOnly = true)
    public Page<ExpansionCardEntity> getAll(
            String search, ExpansionCardType cardType,
            List<Long> manufacturerIds, List<Long> interfaceIds, List<Long> colorIds,
            List<Long> audioChipsetIds, List<Long> protocolIds,
            Double minChannels, Double maxChannels,
            Integer minDigitalAudioBit, Integer maxDigitalAudioBit,
            Double minSampleRateKhz, Double maxSampleRateKhz,
            Pageable pageable) {

        Specification<ExpansionCardEntity> spec = ExpansionCardSpecification.withFilters(
                QueryUtils.cleanSearchToken(search), cardType,
                manufacturerIds, interfaceIds, colorIds, audioChipsetIds, protocolIds,
                minChannels, maxChannels, minDigitalAudioBit, maxDigitalAudioBit,
                minSampleRateKhz, maxSampleRateKhz
        );
        return repository.findAll(spec, pageable);
    }
}
