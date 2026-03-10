package com.github.randdd32.donor_search_backend.service.dictionary;

import com.github.randdd32.donor_search_backend.model.dictionary.FanConnectorEntity;
import com.github.randdd32.donor_search_backend.repository.dictionary.FanConnectorRepository;
import org.springframework.stereotype.Service;

@Service
public class FanConnectorService extends AbstractDictionaryService<FanConnectorEntity, FanConnectorRepository> {
    public FanConnectorService(FanConnectorRepository repository) {
        super(repository, FanConnectorEntity.class);
    }
}
