package com.github.randdd32.donor_search_backend.service.hardware;

import com.github.randdd32.donor_search_backend.core.util.QueryUtils;
import com.github.randdd32.donor_search_backend.model.hardware.PowerSupplyEntity;
import com.github.randdd32.donor_search_backend.repository.hardware.PowerSupplyRepository;
import com.github.randdd32.donor_search_backend.repository.specification.PowerSupplySpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PowerSupplyService extends AbstractHardwareService<PowerSupplyEntity, PowerSupplyRepository> {
    public PowerSupplyService(PowerSupplyRepository repository) {
        super(repository, PowerSupplyEntity.class);
    }

    @Transactional(readOnly = true)
    public Page<PowerSupplyEntity> getAll(
            String search,
            List<Long> manufacturerIds, List<Long> typeIds,
            List<Long> efficiencyIds, List<Long> modularIds, List<Long> colorIds,
            Integer minWattage, Integer maxWattage,
            Integer minLength, Integer maxLength, Pageable pageable) {

        Specification<PowerSupplyEntity> spec = PowerSupplySpecification.withFilters(
                QueryUtils.cleanSearchToken(search),
                manufacturerIds, typeIds, efficiencyIds, modularIds, colorIds,
                minWattage, maxWattage, minLength, maxLength
        );
        return repository.findAll(spec, pageable);
    }
}
