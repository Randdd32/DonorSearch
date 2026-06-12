package com.github.randdd32.donor_search_backend.service.hardware;

import com.github.randdd32.donor_search_backend.core.util.QueryUtils;
import com.github.randdd32.donor_search_backend.model.hardware.OpticalDriveEntity;
import com.github.randdd32.donor_search_backend.repository.hardware.OpticalDriveRepository;
import com.github.randdd32.donor_search_backend.repository.specification.OpticalDriveSpecification;
import com.github.randdd32.donor_search_backend.service.AbstractReadService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OpticalDriveService extends AbstractReadService<OpticalDriveEntity, OpticalDriveRepository> {
    public OpticalDriveService(OpticalDriveRepository repository) {
        super(repository, OpticalDriveEntity.class);
    }

    @Transactional(readOnly = true)
    public Page<OpticalDriveEntity> getAll(
            String search,
            List<Long> manufacturerIds,
            List<Long> formFactorIds,
            List<Long> interfaceIds,
            Pageable pageable) {

        Specification<OpticalDriveEntity> spec = OpticalDriveSpecification.withFilters(
                QueryUtils.cleanSearchToken(search),
                manufacturerIds,
                formFactorIds,
                interfaceIds
        );
        return repository.findAll(spec, pageable);
    }
}
