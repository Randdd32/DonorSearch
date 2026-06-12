package com.github.randdd32.donor_search_backend.service.dictionary;

import com.github.randdd32.donor_search_backend.model.dictionary.EfficiencyRatingEntity;
import com.github.randdd32.donor_search_backend.repository.dictionary.EfficiencyRatingRepository;
import org.springframework.stereotype.Service;

@Service
public class EfficiencyRatingService extends AbstractDictionaryService<EfficiencyRatingEntity, EfficiencyRatingRepository> {
    public EfficiencyRatingService(EfficiencyRatingRepository repository) {
        super(repository, EfficiencyRatingEntity.class);
    }
}
