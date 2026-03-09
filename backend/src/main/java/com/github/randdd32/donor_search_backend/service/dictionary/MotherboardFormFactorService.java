package com.github.randdd32.donor_search_backend.service.dictionary;

import com.github.randdd32.donor_search_backend.model.dictionary.MotherboardFormFactorEntity;
import com.github.randdd32.donor_search_backend.repository.dictionary.MotherboardFormFactorRepository;
import org.springframework.stereotype.Service;

@Service
public class MotherboardFormFactorService extends AbstractDictionaryService<MotherboardFormFactorEntity,
        MotherboardFormFactorRepository> {
    public MotherboardFormFactorService(MotherboardFormFactorRepository repository) {
        super(repository, MotherboardFormFactorEntity.class);
    }
}
