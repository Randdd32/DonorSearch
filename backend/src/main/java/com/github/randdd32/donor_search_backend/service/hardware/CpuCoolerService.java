package com.github.randdd32.donor_search_backend.service.hardware;

import com.github.randdd32.donor_search_backend.core.util.QueryUtils;
import com.github.randdd32.donor_search_backend.model.hardware.CpuCoolerEntity;
import com.github.randdd32.donor_search_backend.repository.hardware.CpuCoolerRepository;
import com.github.randdd32.donor_search_backend.repository.specification.CpuCoolerSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CpuCoolerService extends AbstractHardwareService<CpuCoolerEntity, CpuCoolerRepository> {
    public CpuCoolerService(CpuCoolerRepository repository) {
        super(repository, CpuCoolerEntity.class);
    }

    @Transactional(readOnly = true)
    public Page<CpuCoolerEntity> getAll(
            String search,
            List<Long> manufacturerIds, List<Long> colorIds, List<Long> socketIds,
            Boolean isWaterCooled,
            Integer minHeight, Integer maxHeight,
            Integer minWaterSize, Integer maxWaterSize,
            Integer minRpm, Integer maxRpm,
            Pageable pageable) {

        Specification<CpuCoolerEntity> spec = CpuCoolerSpecification.withFilters(
                QueryUtils.cleanSearchToken(search),
                manufacturerIds, colorIds, socketIds,
                isWaterCooled, minHeight, maxHeight, minWaterSize, maxWaterSize, minRpm, maxRpm
        );
        return repository.findAll(spec, pageable);
    }
}
