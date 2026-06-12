package com.github.randdd32.donor_search_backend.service.hardware;

import com.github.randdd32.donor_search_backend.model.hardware.CaseFanEntity;
import com.github.randdd32.donor_search_backend.repository.hardware.CaseFanRepository;
import com.github.randdd32.donor_search_backend.repository.specification.CaseFanSpecification;
import com.github.randdd32.donor_search_backend.service.AbstractReadService;
import com.github.randdd32.donor_search_backend.web.dto.filter.CaseFanFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CaseFanService extends AbstractReadService<CaseFanEntity, CaseFanRepository> {
    public CaseFanService(CaseFanRepository repository) {
        super(repository, CaseFanEntity.class);
    }

    @Transactional(readOnly = true)
    public Page<CaseFanEntity> getAll(CaseFanFilter filter, Pageable pageable) {
        Specification<CaseFanEntity> spec = CaseFanSpecification.withFilters(filter);
        return repository.findAll(spec, pageable);
    }
}
