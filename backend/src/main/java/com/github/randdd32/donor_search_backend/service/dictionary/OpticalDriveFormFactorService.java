package com.github.randdd32.donor_search_backend.service.dictionary;

import com.github.randdd32.donor_search_backend.model.dictionary.OpticalDriveFormFactorEntity;
import com.github.randdd32.donor_search_backend.repository.dictionary.OpticalDriveFormFactorRepository;
import org.springframework.stereotype.Service;

@Service
public class OpticalDriveFormFactorService extends AbstractDictionaryService<OpticalDriveFormFactorEntity, OpticalDriveFormFactorRepository> {
    public OpticalDriveFormFactorService(OpticalDriveFormFactorRepository repository) {
        super(repository, OpticalDriveFormFactorEntity.class);
    }
}
