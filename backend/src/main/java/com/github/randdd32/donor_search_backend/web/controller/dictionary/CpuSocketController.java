package com.github.randdd32.donor_search_backend.web.controller.dictionary;

import com.github.randdd32.donor_search_backend.core.configuration.Constants;
import com.github.randdd32.donor_search_backend.model.dictionary.CpuSocketEntity;
import com.github.randdd32.donor_search_backend.service.dictionary.CpuSocketService;
import com.github.randdd32.donor_search_backend.web.mapper.dictionary.DictionaryMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.API_URL + Constants.DICTIONARIES_URL + "/cpu-sockets")
public class CpuSocketController extends AbstractDictionaryController<CpuSocketEntity, CpuSocketService> {
    public CpuSocketController(CpuSocketService service, DictionaryMapper mapper) {
        super(service, mapper::toDto);
    }
}
