package com.github.randdd32.donor_search_backend.service.dictionary;

import com.github.randdd32.donor_search_backend.model.dictionary.ColorEntity;
import com.github.randdd32.donor_search_backend.repository.dictionary.ColorRepository;
import org.springframework.stereotype.Service;

@Service
public class ColorService extends AbstractDictionaryService<ColorEntity, ColorRepository> {
    public ColorService(ColorRepository repository) {
        super(repository, ColorEntity.class);
    }
}
