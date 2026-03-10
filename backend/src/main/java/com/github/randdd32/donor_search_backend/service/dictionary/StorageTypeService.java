package com.github.randdd32.donor_search_backend.service.dictionary;

import com.github.randdd32.donor_search_backend.model.dictionary.StorageTypeEntity;
import com.github.randdd32.donor_search_backend.repository.dictionary.StorageTypeRepository;
import org.springframework.stereotype.Service;

@Service
public class StorageTypeService extends AbstractDictionaryService<StorageTypeEntity, StorageTypeRepository> {
    public StorageTypeService(StorageTypeRepository repository) {
        super(repository, StorageTypeEntity.class);
    }
}
