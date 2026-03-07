package com.github.randdd32.donor_search_backend.service.hardware;

import com.github.randdd32.donor_search_backend.core.error.NotFoundException;
import com.github.randdd32.donor_search_backend.core.util.QueryUtils;
import com.github.randdd32.donor_search_backend.repository.hardware.HardwareRepository;
import com.github.randdd32.donor_search_backend.service.AbstractReadService;
import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractHardwareService<T, R extends HardwareRepository<T>> extends AbstractReadService<T, R> {
    protected AbstractHardwareService(R repository, Class<T> entityClass) {
        super(repository, entityClass);
    }

    @Transactional(readOnly = true)
    public T findBestMatch(String rawNameFromInventory) {
        String cleanToken = QueryUtils.cleanSearchToken(rawNameFromInventory);
        if (cleanToken == null) {
            throw new IllegalArgumentException("Search token cannot be null or empty");
        }
        return repository.findMostSimilar(cleanToken)
                .orElseThrow(() -> new NotFoundException("No suitable " + entityClass.getSimpleName() + " found for: " + rawNameFromInventory));
    }
}
