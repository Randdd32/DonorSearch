package com.github.randdd32.donor_search_backend.service.dictionary;

import com.github.randdd32.donor_search_backend.model.dictionary.PowerSupplyTypeEntity;
import com.github.randdd32.donor_search_backend.repository.dictionary.PowerSupplyTypeRepository;
import org.springframework.stereotype.Service;

@Service
public class PowerSupplyTypeService extends AbstractDictionaryService<PowerSupplyTypeEntity, PowerSupplyTypeRepository> {
    public PowerSupplyTypeService(PowerSupplyTypeRepository repository) {
        super(repository, PowerSupplyTypeEntity.class);
    }
}
