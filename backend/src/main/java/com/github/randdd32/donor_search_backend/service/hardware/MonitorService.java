package com.github.randdd32.donor_search_backend.service.hardware;

import com.github.randdd32.donor_search_backend.model.hardware.MonitorEntity;
import com.github.randdd32.donor_search_backend.repository.hardware.MonitorRepository;
import com.github.randdd32.donor_search_backend.repository.specification.MonitorSpecification;
import com.github.randdd32.donor_search_backend.service.AbstractReadService;
import com.github.randdd32.donor_search_backend.web.dto.filter.MonitorFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MonitorService extends AbstractReadService<MonitorEntity, MonitorRepository> {
    public MonitorService(MonitorRepository repository) {
        super(repository, MonitorEntity.class);
    }

    @Transactional(readOnly = true)
    public Page<MonitorEntity> getAll(MonitorFilter filter, Pageable pageable) {
        Specification<MonitorEntity> spec = MonitorSpecification.withFilters(filter);
        return repository.findAll(spec, pageable);
    }
}
