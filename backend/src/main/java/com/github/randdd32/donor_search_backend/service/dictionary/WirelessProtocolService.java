package com.github.randdd32.donor_search_backend.service.dictionary;

import com.github.randdd32.donor_search_backend.model.dictionary.WirelessProtocolEntity;
import com.github.randdd32.donor_search_backend.repository.dictionary.WirelessProtocolRepository;
import org.springframework.stereotype.Service;

@Service
public class WirelessProtocolService extends AbstractDictionaryService<WirelessProtocolEntity, WirelessProtocolRepository> {
    public WirelessProtocolService(WirelessProtocolRepository repository) {
        super(repository, WirelessProtocolEntity.class);
    }
}
