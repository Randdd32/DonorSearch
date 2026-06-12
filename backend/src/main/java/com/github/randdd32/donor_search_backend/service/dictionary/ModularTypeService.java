package com.github.randdd32.donor_search_backend.service.dictionary;

import com.github.randdd32.donor_search_backend.model.dictionary.ModularTypeEntity;
import com.github.randdd32.donor_search_backend.repository.dictionary.ModularTypeRepository;
import org.springframework.stereotype.Service;

@Service
public class ModularTypeService extends AbstractDictionaryService<ModularTypeEntity, ModularTypeRepository> {
    public ModularTypeService(ModularTypeRepository repository) {
        super(repository, ModularTypeEntity.class);
    }
}
