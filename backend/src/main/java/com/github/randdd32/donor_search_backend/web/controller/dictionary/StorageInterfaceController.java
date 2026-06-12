package com.github.randdd32.donor_search_backend.web.controller.dictionary;

import com.github.randdd32.donor_search_backend.core.configuration.Constants;
import com.github.randdd32.donor_search_backend.model.dictionary.StorageInterfaceEntity;
import com.github.randdd32.donor_search_backend.service.dictionary.StorageInterfaceService;
import com.github.randdd32.donor_search_backend.web.mapper.dictionary.DictionaryMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.API_URL + Constants.DICTIONARIES_URL + "/storage-interfaces")
public class StorageInterfaceController extends AbstractDictionaryController<StorageInterfaceEntity, StorageInterfaceService> {
    public StorageInterfaceController(StorageInterfaceService service, DictionaryMapper mapper) {
        super(service, mapper::toDto);
    }
}
