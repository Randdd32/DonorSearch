package com.github.randdd32.donor_search_backend.service.hardware;

import com.github.randdd32.donor_search_backend.model.hardware.MemoryEntity;
import com.github.randdd32.donor_search_backend.repository.hardware.MemoryRepository;
import com.github.randdd32.donor_search_backend.repository.specification.MemorySpecification;
import com.github.randdd32.donor_search_backend.service.AbstractReadService;
import com.github.randdd32.donor_search_backend.web.dto.filter.MemoryFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemoryService extends AbstractReadService<MemoryEntity, MemoryRepository> {
    public MemoryService(MemoryRepository repository) {
        super(repository, MemoryEntity.class);
    }

    @Transactional(readOnly = true)
    public Page<MemoryEntity> getAll(MemoryFilter filter, Pageable pageable) {
        Specification<MemoryEntity> spec = MemorySpecification.withFilters(filter);
        return repository.findAll(spec, pageable);
    }
}
