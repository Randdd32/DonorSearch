package com.github.randdd32.donor_search_backend.service.dictionary;

import com.github.randdd32.donor_search_backend.model.dictionary.OpticalDriveFormFactorEntity;
import com.github.randdd32.donor_search_backend.repository.dictionary.OpticalDriveFormFactorRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OpticalDriveFormFactorService extends AbstractDictionaryService<OpticalDriveFormFactorEntity, OpticalDriveFormFactorRepository> {
    @Getter
    private final OpticalDriveFormFactorRepository repository;

    @Getter
    private final Class<OpticalDriveFormFactorEntity> entityClass = OpticalDriveFormFactorEntity.class;
}
