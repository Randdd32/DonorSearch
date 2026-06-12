package com.github.randdd32.donor_search_backend.web.controller.dictionary;

import com.github.randdd32.donor_search_backend.core.configuration.Constants;
import com.github.randdd32.donor_search_backend.model.dictionary.ColorEntity;
import com.github.randdd32.donor_search_backend.service.dictionary.ColorService;
import com.github.randdd32.donor_search_backend.web.mapper.dictionary.DictionaryMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.API_URL + Constants.DICTIONARIES_URL + "/colors")
public class ColorController extends AbstractDictionaryController<ColorEntity, ColorService> {
    public ColorController(ColorService service, DictionaryMapper mapper) {
        super(service, mapper::toDto);
    }
}
