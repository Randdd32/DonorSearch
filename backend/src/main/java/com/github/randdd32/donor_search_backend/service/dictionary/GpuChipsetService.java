package com.github.randdd32.donor_search_backend.service.dictionary;

import com.github.randdd32.donor_search_backend.model.dictionary.GpuChipsetEntity;
import com.github.randdd32.donor_search_backend.repository.dictionary.GpuChipsetRepository;
import org.springframework.stereotype.Service;

@Service
public class GpuChipsetService extends AbstractDictionaryService<GpuChipsetEntity, GpuChipsetRepository> {
    public GpuChipsetService(GpuChipsetRepository repository) {
        super(repository, GpuChipsetEntity.class);
    }
}
