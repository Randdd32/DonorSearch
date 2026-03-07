package com.github.randdd32.donor_search_backend.service.dictionary;

import com.github.randdd32.donor_search_backend.model.dictionary.MemoryTypeEntity;
import com.github.randdd32.donor_search_backend.repository.dictionary.MemoryTypeRepository;
import org.springframework.stereotype.Service;

@Service
public class MemoryTypeService extends AbstractDictionaryService<MemoryTypeEntity, MemoryTypeRepository> {
    public MemoryTypeService(MemoryTypeRepository repository) {
        super(repository, MemoryTypeEntity.class);
    }
}
