package com.github.randdd32.donor_search_backend.web.controller.integration.dictionary;

import com.github.randdd32.donor_search_backend.core.configuration.Constants;
import com.github.randdd32.donor_search_backend.service.integration.dictionary.InfraManufacturerService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.API_URL + Constants.INFRA_URL + Constants.DICTIONARIES_URL + "/manufacturers")
public class InfraManufacturerController extends AbstractInfraDictionaryController {
    public InfraManufacturerController(InfraManufacturerService service) {
        super(service);
    }
}
