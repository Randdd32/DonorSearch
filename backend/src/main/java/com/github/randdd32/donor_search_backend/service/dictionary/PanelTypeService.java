package com.github.randdd32.donor_search_backend.service.dictionary;

import com.github.randdd32.donor_search_backend.model.dictionary.PanelTypeEntity;
import com.github.randdd32.donor_search_backend.repository.dictionary.PanelTypeRepository;
import org.springframework.stereotype.Service;

@Service
public class PanelTypeService extends AbstractDictionaryService<PanelTypeEntity, PanelTypeRepository> {
    public PanelTypeService(PanelTypeRepository repository) {
        super(repository, PanelTypeEntity.class);
    }
}
