package com.github.randdd32.donor_search_backend.service.dictionary;

import com.github.randdd32.donor_search_backend.model.dictionary.ExpansionInterfaceEntity;
import com.github.randdd32.donor_search_backend.repository.dictionary.ExpansionInterfaceRepository;
import org.springframework.stereotype.Service;

@Service
public class ExpansionInterfaceService extends AbstractDictionaryService<ExpansionInterfaceEntity, ExpansionInterfaceRepository> {
    public ExpansionInterfaceService(ExpansionInterfaceRepository repository) {
        super(repository, ExpansionInterfaceEntity.class);
    }
}
