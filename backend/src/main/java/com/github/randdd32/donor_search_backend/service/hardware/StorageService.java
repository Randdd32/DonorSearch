package com.github.randdd32.donor_search_backend.service.hardware;

import com.github.randdd32.donor_search_backend.core.util.QueryUtils;
import com.github.randdd32.donor_search_backend.model.hardware.StorageEntity;
import com.github.randdd32.donor_search_backend.repository.hardware.StorageRepository;
import com.github.randdd32.donor_search_backend.repository.specification.StorageSpecification;
import com.github.randdd32.donor_search_backend.service.AbstractReadService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StorageService extends AbstractReadService<StorageEntity, StorageRepository> {
    public StorageService(StorageRepository repository) {
        super(repository, StorageEntity.class);
    }

    @Transactional(readOnly = true)
    public Page<StorageEntity> getAll(
            String search,
            List<Long> manufacturerIds, List<Long> typeIds,
            List<Long> formFactorIds, List<Long> colorIds, List<Long> interfaceIds,
            Boolean isExternal,
            Integer minCapacity, Integer maxCapacity,
            Integer minCache, Integer maxCache,
            Integer minRpm, Integer maxRpm, Pageable pageable) {

        Specification<StorageEntity> spec = StorageSpecification.withFilters(
                QueryUtils.cleanSearchToken(search),
                manufacturerIds, typeIds, formFactorIds, colorIds, interfaceIds,
                isExternal, minCapacity, maxCapacity, minCache, maxCache, minRpm, maxRpm
        );
        return repository.findAll(spec, pageable);
    }
}
