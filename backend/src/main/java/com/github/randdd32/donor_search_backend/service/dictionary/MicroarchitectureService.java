package com.github.randdd32.donor_search_backend.service.dictionary;

import com.github.randdd32.donor_search_backend.model.dictionary.MicroarchitectureEntity;
import com.github.randdd32.donor_search_backend.repository.dictionary.MicroarchitectureRepository;
import org.springframework.stereotype.Service;

@Service
public class MicroarchitectureService extends AbstractDictionaryService<MicroarchitectureEntity, MicroarchitectureRepository> {
    public MicroarchitectureService(MicroarchitectureRepository repository) {
        super(repository, MicroarchitectureEntity.class);
    }
}
