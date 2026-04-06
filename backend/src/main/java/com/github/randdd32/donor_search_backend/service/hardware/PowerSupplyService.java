package com.github.randdd32.donor_search_backend.service.hardware;

import com.github.randdd32.donor_search_backend.model.hardware.PowerSupplyEntity;
import com.github.randdd32.donor_search_backend.repository.hardware.PowerSupplyRepository;
import com.github.randdd32.donor_search_backend.repository.specification.PowerSupplySpecification;
import com.github.randdd32.donor_search_backend.service.AbstractReadService;
import com.github.randdd32.donor_search_backend.web.dto.filter.PowerSupplyFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PowerSupplyService extends AbstractReadService<PowerSupplyEntity, PowerSupplyRepository> {
    public PowerSupplyService(PowerSupplyRepository repository) {
        super(repository, PowerSupplyEntity.class);
    }

    @Transactional(readOnly = true)
    public Page<PowerSupplyEntity> getAll(PowerSupplyFilter filter, Pageable pageable) {
        Specification<PowerSupplyEntity> spec = PowerSupplySpecification.withFilters(filter);
        return repository.findAll(spec, pageable);
    }
}
