package com.github.randdd32.donor_search_backend.service.dictionary;

import com.github.randdd32.donor_search_backend.model.dictionary.IntegratedGraphicsEntity;
import com.github.randdd32.donor_search_backend.repository.dictionary.IntegratedGraphicsRepository;
import org.springframework.stereotype.Service;

@Service
public class IntegratedGraphicsService extends AbstractDictionaryService<IntegratedGraphicsEntity, IntegratedGraphicsRepository> {
    public IntegratedGraphicsService(IntegratedGraphicsRepository repository) {
        super(repository, IntegratedGraphicsEntity.class);
    }
}
