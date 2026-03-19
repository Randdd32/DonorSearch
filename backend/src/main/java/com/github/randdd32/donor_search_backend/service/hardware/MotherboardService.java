package com.github.randdd32.donor_search_backend.service.hardware;

import com.github.randdd32.donor_search_backend.core.util.QueryUtils;
import com.github.randdd32.donor_search_backend.model.hardware.MotherboardEntity;
import com.github.randdd32.donor_search_backend.repository.hardware.MotherboardRepository;
import com.github.randdd32.donor_search_backend.repository.specification.MotherboardSpecification;
import com.github.randdd32.donor_search_backend.service.AbstractReadService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MotherboardService extends AbstractReadService<MotherboardEntity, MotherboardRepository> {
    public MotherboardService(MotherboardRepository repository) {
        super(repository, MotherboardEntity.class);
    }

    @Transactional(readOnly = true)
    public Page<MotherboardEntity> getAll(
            String search,
            List<Long> manufacturerIds, List<Long> socketIds,
            List<Long> formFactorIds, List<Long> memoryTypeIds,
            Integer minMaxMemoryGb, Integer minMemorySlots, Integer minMemorySpeedMhz,
            Boolean eccSupport, Boolean usesBackConnect, Pageable pageable) {

        Specification<MotherboardEntity> spec = MotherboardSpecification.withFilters(
                QueryUtils.cleanSearchToken(search),
                manufacturerIds, socketIds, formFactorIds, memoryTypeIds,
                minMaxMemoryGb, minMemorySlots, minMemorySpeedMhz, eccSupport, usesBackConnect
        );
        return repository.findAll(spec, pageable);
    }
}
