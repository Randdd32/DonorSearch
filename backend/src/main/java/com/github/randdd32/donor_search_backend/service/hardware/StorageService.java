package com.github.randdd32.donor_search_backend.service.hardware;

import com.github.randdd32.donor_search_backend.model.hardware.StorageEntity;
import com.github.randdd32.donor_search_backend.repository.hardware.StorageRepository;
import com.github.randdd32.donor_search_backend.repository.specification.StorageSpecification;
import com.github.randdd32.donor_search_backend.service.AbstractReadService;
import com.github.randdd32.donor_search_backend.web.dto.filter.StorageFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StorageService extends AbstractReadService<StorageEntity, StorageRepository> {
    public StorageService(StorageRepository repository) {
        super(repository, StorageEntity.class);
    }

    @Transactional(readOnly = true)
    public Page<StorageEntity> getAll(StorageFilter filter, Pageable pageable) {
        Specification<StorageEntity> spec = StorageSpecification.withFilters(filter);
        return repository.findAll(spec, pageable);
    }
}
