package com.github.randdd32.donor_search_backend.service.hardware;

import com.github.randdd32.donor_search_backend.core.util.QueryUtils;
import com.github.randdd32.donor_search_backend.model.hardware.MonitorEntity;
import com.github.randdd32.donor_search_backend.repository.hardware.MonitorRepository;
import com.github.randdd32.donor_search_backend.repository.specification.MonitorSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MonitorService extends AbstractHardwareService<MonitorEntity, MonitorRepository> {
    public MonitorService(MonitorRepository repository) {
        super(repository, MonitorEntity.class);
    }

    @Transactional(readOnly = true)
    public Page<MonitorEntity> getAll(
            String search,
            List<Long> manufacturerIds, List<Long> resolutionIds,
            List<Long> panelTypeIds, List<Long> aspectRatioIds,
            Double minScreenSize, Double maxScreenSize,
            Integer minRefreshRate, Integer maxRefreshRate,
            Double minResponseTime, Double maxResponseTime,
            Pageable pageable) {

        Specification<MonitorEntity> spec = MonitorSpecification.withFilters(
                QueryUtils.cleanSearchToken(search),
                manufacturerIds, resolutionIds, panelTypeIds, aspectRatioIds,
                minScreenSize, maxScreenSize, minRefreshRate, maxRefreshRate, minResponseTime, maxResponseTime
        );
        return repository.findAll(spec, pageable);
    }
}
