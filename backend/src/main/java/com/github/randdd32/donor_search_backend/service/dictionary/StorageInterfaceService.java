package com.github.randdd32.donor_search_backend.service.dictionary;

import com.github.randdd32.donor_search_backend.model.dictionary.StorageInterfaceEntity;
import com.github.randdd32.donor_search_backend.repository.dictionary.StorageInterfaceRepository;
import org.springframework.stereotype.Service;

@Service
public class StorageInterfaceService extends AbstractDictionaryService<StorageInterfaceEntity, StorageInterfaceRepository> {
    public StorageInterfaceService(StorageInterfaceRepository repository) {
        super(repository, StorageInterfaceEntity.class);
    }
}
