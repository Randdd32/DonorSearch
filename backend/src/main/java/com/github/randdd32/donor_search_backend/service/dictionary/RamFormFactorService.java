package com.github.randdd32.donor_search_backend.service.dictionary;

import com.github.randdd32.donor_search_backend.model.dictionary.RamFormFactorEntity;
import com.github.randdd32.donor_search_backend.repository.dictionary.RamFormFactorRepository;
import org.springframework.stereotype.Service;

@Service
public class RamFormFactorService extends AbstractDictionaryService<RamFormFactorEntity, RamFormFactorRepository> {
    public RamFormFactorService(RamFormFactorRepository repository) {
        super(repository, RamFormFactorEntity.class);
    }
}
