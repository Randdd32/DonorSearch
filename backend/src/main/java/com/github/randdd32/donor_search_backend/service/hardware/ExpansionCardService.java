package com.github.randdd32.donor_search_backend.service.hardware;

import com.github.randdd32.donor_search_backend.model.hardware.ExpansionCardEntity;
import com.github.randdd32.donor_search_backend.repository.hardware.ExpansionCardRepository;
import com.github.randdd32.donor_search_backend.repository.specification.ExpansionCardSpecification;
import com.github.randdd32.donor_search_backend.service.AbstractReadService;
import com.github.randdd32.donor_search_backend.web.dto.filter.ExpansionCardFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExpansionCardService extends AbstractReadService<ExpansionCardEntity, ExpansionCardRepository> {
    public ExpansionCardService(ExpansionCardRepository repository) {
        super(repository, ExpansionCardEntity.class);
    }

    @Transactional(readOnly = true)
    public Page<ExpansionCardEntity> getAll(ExpansionCardFilter filter, Pageable pageable) {
        Specification<ExpansionCardEntity> spec = ExpansionCardSpecification.withFilters(filter);
        return repository.findAll(spec, pageable);
    }
}
