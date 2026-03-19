package com.github.randdd32.donor_search_backend.service.hardware;

import com.github.randdd32.donor_search_backend.core.util.QueryUtils;
import com.github.randdd32.donor_search_backend.model.hardware.CaseFanEntity;
import com.github.randdd32.donor_search_backend.repository.hardware.CaseFanRepository;
import com.github.randdd32.donor_search_backend.repository.specification.CaseFanSpecification;
import com.github.randdd32.donor_search_backend.service.AbstractReadService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CaseFanService extends AbstractReadService<CaseFanEntity, CaseFanRepository> {
    public CaseFanService(CaseFanRepository repository) {
        super(repository, CaseFanEntity.class);
    }

    @Transactional(readOnly = true)
    public Page<CaseFanEntity> getAll(
            String search,
            List<Long> manufacturerIds,
            List<Long> colorIds,
            List<Long> connectorIds,
            List<Integer> sizes,
            Boolean pwm,
            Integer minRpm, Integer maxRpm,
            Integer minAirflow, Integer maxAirflow,
            Pageable pageable) {

        Specification<CaseFanEntity> spec = CaseFanSpecification.withFilters(
                QueryUtils.cleanSearchToken(search),
                manufacturerIds, colorIds, connectorIds,
                sizes, pwm, minRpm, maxRpm, minAirflow, maxAirflow
        );
        return repository.findAll(spec, pageable);
    }
}
