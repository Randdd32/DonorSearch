package com.github.randdd32.donor_search_backend.service.hardware;

import com.github.randdd32.donor_search_backend.model.hardware.MotherboardEntity;
import com.github.randdd32.donor_search_backend.repository.hardware.MotherboardRepository;
import com.github.randdd32.donor_search_backend.repository.specification.MotherboardSpecification;
import com.github.randdd32.donor_search_backend.service.AbstractReadService;
import com.github.randdd32.donor_search_backend.web.dto.filter.MotherboardFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MotherboardService extends AbstractReadService<MotherboardEntity, MotherboardRepository> {
    public MotherboardService(MotherboardRepository repository) {
        super(repository, MotherboardEntity.class);
    }

    @Transactional(readOnly = true)
    public Page<MotherboardEntity> getAll(MotherboardFilter filter, Pageable pageable) {
        Specification<MotherboardEntity> spec = MotherboardSpecification.withFilters(filter);
        return repository.findAll(spec, pageable);
    }
}
