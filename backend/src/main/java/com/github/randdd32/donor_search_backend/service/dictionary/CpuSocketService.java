package com.github.randdd32.donor_search_backend.service.dictionary;

import com.github.randdd32.donor_search_backend.model.dictionary.CpuSocketEntity;
import com.github.randdd32.donor_search_backend.repository.dictionary.CpuSocketRepository;
import org.springframework.stereotype.Service;

@Service
public class CpuSocketService extends AbstractDictionaryService<CpuSocketEntity, CpuSocketRepository> {
    public CpuSocketService(CpuSocketRepository repository) {
        super(repository,  CpuSocketEntity.class);
    }
}
