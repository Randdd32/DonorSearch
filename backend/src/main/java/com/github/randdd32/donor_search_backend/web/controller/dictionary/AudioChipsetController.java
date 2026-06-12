package com.github.randdd32.donor_search_backend.web.controller.dictionary;

import com.github.randdd32.donor_search_backend.core.configuration.Constants;
import com.github.randdd32.donor_search_backend.model.dictionary.AudioChipsetEntity;
import com.github.randdd32.donor_search_backend.service.dictionary.AudioChipsetService;
import com.github.randdd32.donor_search_backend.web.mapper.dictionary.DictionaryMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.API_URL + Constants.DICTIONARIES_URL + "/audio-chipsets")
public class AudioChipsetController extends AbstractDictionaryController<AudioChipsetEntity, AudioChipsetService> {
    public AudioChipsetController(AudioChipsetService service, DictionaryMapper mapper) {
        super(service, mapper::toDto);
    }
}
