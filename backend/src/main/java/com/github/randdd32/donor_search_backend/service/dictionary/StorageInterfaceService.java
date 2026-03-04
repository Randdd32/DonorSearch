package com.github.randdd32.donor_search_backend.service.dictionary;

import com.github.randdd32.donor_search_backend.model.dictionary.StorageInterfaceEntity;
import com.github.randdd32.donor_search_backend.repository.dictionary.StorageInterfaceRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StorageInterfaceService extends AbstractDictionaryService<StorageInterfaceEntity, StorageInterfaceRepository> {
    @Getter
    private final StorageInterfaceRepository repository;

    @Getter
    private final Class<StorageInterfaceEntity> entityClass = StorageInterfaceEntity.class;
}
