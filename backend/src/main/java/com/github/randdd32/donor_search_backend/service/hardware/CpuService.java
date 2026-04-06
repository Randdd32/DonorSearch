package com.github.randdd32.donor_search_backend.service.hardware;

import com.github.randdd32.donor_search_backend.model.hardware.CpuEntity;
import com.github.randdd32.donor_search_backend.repository.hardware.CpuRepository;
import com.github.randdd32.donor_search_backend.repository.specification.CpuSpecification;
import com.github.randdd32.donor_search_backend.service.AbstractReadService;
import com.github.randdd32.donor_search_backend.web.dto.filter.CpuFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CpuService extends AbstractReadService<CpuEntity, CpuRepository> {
    public CpuService(CpuRepository repository) {
        super(repository, CpuEntity.class);
    }

    @Transactional(readOnly = true)
    public Page<CpuEntity> getAll(CpuFilter filter, Pageable pageable) {
        Specification<CpuEntity> spec = CpuSpecification.withFilters(filter);
        return repository.findAll(spec, pageable);
    }
}
