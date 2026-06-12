package com.github.randdd32.donor_search_backend.service.dictionary;

import com.github.randdd32.donor_search_backend.model.dictionary.SidePanelEntity;
import com.github.randdd32.donor_search_backend.repository.dictionary.SidePanelRepository;
import org.springframework.stereotype.Service;

@Service
public class SidePanelService extends AbstractDictionaryService<SidePanelEntity, SidePanelRepository> {
    public SidePanelService(SidePanelRepository repository) {
        super(repository, SidePanelEntity.class);
    }
}
