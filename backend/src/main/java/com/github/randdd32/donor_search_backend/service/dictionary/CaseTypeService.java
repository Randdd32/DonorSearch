package com.github.randdd32.donor_search_backend.service.dictionary;

import com.github.randdd32.donor_search_backend.model.dictionary.CaseTypeEntity;
import com.github.randdd32.donor_search_backend.repository.dictionary.CaseTypeRepository;
import org.springframework.stereotype.Service;

@Service
public class CaseTypeService extends AbstractDictionaryService<CaseTypeEntity, CaseTypeRepository> {
    public CaseTypeService(CaseTypeRepository repository) {
        super(repository, CaseTypeEntity.class);
    }
}
