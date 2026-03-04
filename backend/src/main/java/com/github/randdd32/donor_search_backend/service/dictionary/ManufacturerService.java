package com.github.randdd32.donor_search_backend.service.dictionary;

import com.github.randdd32.donor_search_backend.model.dictionary.ManufacturerEntity;
import com.github.randdd32.donor_search_backend.repository.dictionary.ManufacturerRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ManufacturerService extends AbstractDictionaryService<ManufacturerEntity, ManufacturerRepository> {
    @Getter
    private final ManufacturerRepository repository;

    @Getter
    private final Class<ManufacturerEntity> entityClass = ManufacturerEntity.class;
}
