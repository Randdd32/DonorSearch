package com.github.randdd32.donor_search_backend.web.controller.integration.dictionary;

import com.github.randdd32.donor_search_backend.core.configuration.Constants;
import com.github.randdd32.donor_search_backend.service.integration.dictionary.InfraFloorService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.API_URL + Constants.INFRA_URL + Constants.DICTIONARIES_URL + "/floors")
public class InfraFloorController extends AbstractInfraDictionaryController {
    public InfraFloorController(InfraFloorService service) {
        super(service);
    }
}
