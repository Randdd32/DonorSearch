package com.github.randdd32.donor_search_backend.service.hardware;

import com.github.randdd32.donor_search_backend.core.util.QueryUtils;
import com.github.randdd32.donor_search_backend.model.hardware.CaseEntity;
import com.github.randdd32.donor_search_backend.repository.hardware.CaseRepository;
import com.github.randdd32.donor_search_backend.repository.specification.CaseSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CaseService extends AbstractHardwareService<CaseEntity, CaseRepository> {
    public CaseService(CaseRepository repository) {
        super(repository, CaseEntity.class);
    }

    @Transactional(readOnly = true)
    public Page<CaseEntity> getAll(
            String search,
            List<Long> manufacturerIds, List<Long> caseTypeIds,
            List<Long> colorIds, List<Long> sidePanelIds,
            Integer minLength, Integer maxLength,
            Integer minWidth, Integer maxWidth,
            Integer minHeight, Integer maxHeight,
            Integer minInt35Bays, Integer minExpansionSlots,
            List<Long> moboFormFactorIds, List<Long> frontPanelUsbIds,
            Pageable pageable) {

        Specification<CaseEntity> spec = CaseSpecification.withFilters(
                QueryUtils.cleanSearchToken(search),
                manufacturerIds, caseTypeIds, colorIds, sidePanelIds,
                minLength, maxLength, minWidth, maxWidth, minHeight, maxHeight,
                minInt35Bays, minExpansionSlots, moboFormFactorIds, frontPanelUsbIds
        );
        return repository.findAll(spec, pageable);
    }
}
