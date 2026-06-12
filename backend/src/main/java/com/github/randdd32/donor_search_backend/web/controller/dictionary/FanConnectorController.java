package com.github.randdd32.donor_search_backend.web.controller.dictionary;

import com.github.randdd32.donor_search_backend.core.configuration.Constants;
import com.github.randdd32.donor_search_backend.model.dictionary.FanConnectorEntity;
import com.github.randdd32.donor_search_backend.service.dictionary.FanConnectorService;
import com.github.randdd32.donor_search_backend.web.mapper.dictionary.DictionaryMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.API_URL + Constants.DICTIONARIES_URL + "/fan-connectors")
public class FanConnectorController extends AbstractDictionaryController<FanConnectorEntity, FanConnectorService> {
    public FanConnectorController(FanConnectorService service, DictionaryMapper mapper) {
        super(service, mapper::toDto);
    }
}
