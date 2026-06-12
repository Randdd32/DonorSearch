package com.github.randdd32.donor_search_backend.service.dictionary;

import com.github.randdd32.donor_search_backend.model.dictionary.AudioChipsetEntity;
import com.github.randdd32.donor_search_backend.repository.dictionary.AudioChipsetRepository;
import org.springframework.stereotype.Service;

@Service
public class AudioChipsetService extends AbstractDictionaryService<AudioChipsetEntity, AudioChipsetRepository> {
    public AudioChipsetService(AudioChipsetRepository repository) {
        super(repository, AudioChipsetEntity.class);
    }
}
