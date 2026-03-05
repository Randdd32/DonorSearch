package com.github.randdd32.donor_search_backend.service.dictionary;

import com.github.randdd32.donor_search_backend.model.dictionary.ManufacturerEntity;
import com.github.randdd32.donor_search_backend.repository.dictionary.ManufacturerRepository;
import org.springframework.stereotype.Service;

@Service
public class ManufacturerService extends AbstractDictionaryService<ManufacturerEntity, ManufacturerRepository> {
    public ManufacturerService(ManufacturerRepository repository) {
        super(repository, ManufacturerEntity.class);
    }
}
