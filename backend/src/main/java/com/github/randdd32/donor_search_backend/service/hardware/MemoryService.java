package com.github.randdd32.donor_search_backend.service.hardware;

import com.github.randdd32.donor_search_backend.core.util.QueryUtils;
import com.github.randdd32.donor_search_backend.model.hardware.MemoryEntity;
import com.github.randdd32.donor_search_backend.repository.hardware.MemoryRepository;
import com.github.randdd32.donor_search_backend.repository.specification.MemorySpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MemoryService extends AbstractHardwareService<MemoryEntity, MemoryRepository> {
    public MemoryService(MemoryRepository repository) {
        super(repository, MemoryEntity.class);
    }

    @Transactional(readOnly = true)
    public Page<MemoryEntity> getAll(
            String search,
            List<Long> manufacturerIds, List<Long> formFactorIds,
            List<Long> memoryTypeIds, List<Long> colorIds,
            Integer minFreq, Integer maxFreq,
            Integer minCount, Integer maxCount,
            Integer minSize, Integer maxSize,
            Integer minCas, Integer maxCas,
            Boolean isEcc, Boolean isRegistered, Pageable pageable) {

        Specification<MemoryEntity> spec = MemorySpecification.withFilters(
                QueryUtils.cleanSearchToken(search),
                manufacturerIds, formFactorIds, memoryTypeIds, colorIds,
                minFreq, maxFreq, minCount, maxCount, minSize, maxSize, minCas, maxCas,
                isEcc, isRegistered
        );
        return repository.findAll(spec, pageable);
    }
}
