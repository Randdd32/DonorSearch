package com.github.randdd32.donor_search_backend.service.hardware;

import com.github.randdd32.donor_search_backend.model.hardware.CpuCoolerEntity;
import com.github.randdd32.donor_search_backend.repository.hardware.CpuCoolerRepository;
import com.github.randdd32.donor_search_backend.repository.specification.CpuCoolerSpecification;
import com.github.randdd32.donor_search_backend.service.AbstractReadService;
import com.github.randdd32.donor_search_backend.web.dto.filter.CpuCoolerFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CpuCoolerService extends AbstractReadService<CpuCoolerEntity, CpuCoolerRepository> {
    public CpuCoolerService(CpuCoolerRepository repository) {
        super(repository, CpuCoolerEntity.class);
    }

    @Transactional(readOnly = true)
    public Page<CpuCoolerEntity> getAll(CpuCoolerFilter filter, Pageable pageable) {
        Specification<CpuCoolerEntity> spec = CpuCoolerSpecification.withFilters(filter);
        return repository.findAll(spec, pageable);
    }
}
