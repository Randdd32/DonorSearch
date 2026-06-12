package com.github.randdd32.donor_search_backend.service.dictionary;

import com.github.randdd32.donor_search_backend.model.dictionary.StorageFormFactorEntity;
import com.github.randdd32.donor_search_backend.repository.dictionary.StorageFormFactorRepository;
import org.springframework.stereotype.Service;

@Service
public class StorageFormFactorService extends AbstractDictionaryService<StorageFormFactorEntity, StorageFormFactorRepository> {
    public StorageFormFactorService(StorageFormFactorRepository repository) {
        super(repository, StorageFormFactorEntity.class);
    }
}
