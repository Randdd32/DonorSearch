package com.github.randdd32.donor_search_backend.service.dictionary;

import com.github.randdd32.donor_search_backend.model.dictionary.AspectRatioEntity;
import com.github.randdd32.donor_search_backend.repository.dictionary.AspectRatioRepository;
import org.springframework.stereotype.Service;

@Service
public class AspectRatioService extends AbstractDictionaryService<AspectRatioEntity, AspectRatioRepository> {
    public AspectRatioService(AspectRatioRepository repository) {
        super(repository, AspectRatioEntity.class);
    }
}
