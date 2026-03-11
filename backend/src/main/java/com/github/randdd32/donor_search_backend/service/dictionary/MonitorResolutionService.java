package com.github.randdd32.donor_search_backend.service.dictionary;

import com.github.randdd32.donor_search_backend.model.dictionary.MonitorResolutionEntity;
import com.github.randdd32.donor_search_backend.repository.dictionary.MonitorResolutionRepository;
import org.springframework.stereotype.Service;

@Service
public class MonitorResolutionService extends AbstractDictionaryService<MonitorResolutionEntity, MonitorResolutionRepository> {
    public MonitorResolutionService(MonitorResolutionRepository repository) {
        super(repository, MonitorResolutionEntity.class);
    }
}
