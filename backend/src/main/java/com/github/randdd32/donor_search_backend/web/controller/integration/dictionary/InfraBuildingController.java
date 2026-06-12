package com.github.randdd32.donor_search_backend.web.controller.integration.dictionary;

import com.github.randdd32.donor_search_backend.core.configuration.Constants;
import com.github.randdd32.donor_search_backend.service.integration.dictionary.InfraBuildingService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.API_URL + Constants.INFRA_URL + Constants.DICTIONARIES_URL + "/buildings")
public class InfraBuildingController extends AbstractInfraDictionaryController {
    public InfraBuildingController(InfraBuildingService service) {
        super(service);
    }
}
