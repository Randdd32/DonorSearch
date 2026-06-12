package com.github.randdd32.donor_search_backend.service.dictionary;

import com.github.randdd32.donor_search_backend.model.dictionary.FrontPanelUsbEntity;
import com.github.randdd32.donor_search_backend.repository.dictionary.FrontPanelUsbRepository;
import org.springframework.stereotype.Service;

@Service
public class FrontPanelUsbService extends AbstractDictionaryService<FrontPanelUsbEntity, FrontPanelUsbRepository> {
    public FrontPanelUsbService(FrontPanelUsbRepository repository) {
        super(repository, FrontPanelUsbEntity.class);
    }
}
