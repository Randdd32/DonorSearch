package com.github.randdd32.donor_search_backend.web.controller.dictionary;

import com.github.randdd32.donor_search_backend.core.configuration.Constants;
import com.github.randdd32.donor_search_backend.model.dictionary.PowerSupplyTypeEntity;
import com.github.randdd32.donor_search_backend.service.dictionary.PowerSupplyTypeService;
import com.github.randdd32.donor_search_backend.web.mapper.dictionary.DictionaryMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.API_URL + Constants.DICTIONARIES_URL + "/power-supply-types")
public class PowerSupplyTypeController extends AbstractDictionaryController<PowerSupplyTypeEntity, PowerSupplyTypeService> {
    public PowerSupplyTypeController(PowerSupplyTypeService service, DictionaryMapper mapper) {
        super(service, mapper::toDto);
    }
}
