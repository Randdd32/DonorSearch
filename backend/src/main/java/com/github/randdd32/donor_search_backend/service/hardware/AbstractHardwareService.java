package com.github.randdd32.donor_search_backend.service.hardware;

import com.github.randdd32.donor_search_backend.core.error.NotFoundException;
import com.github.randdd32.donor_search_backend.core.util.QueryUtils;
import com.github.randdd32.donor_search_backend.repository.hardware.HardwareRepository;
import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractHardwareService<T, R extends HardwareRepository<T>> {
    protected abstract R getRepository();
    protected abstract Class<T> getEntityClass();

    @Transactional(readOnly = true)
    public T getById(Long id) {
        return getRepository().findById(id)
                .orElseThrow(() -> new NotFoundException(getEntityClass(), id));
    }

    @Transactional(readOnly = true)
    public T findBestMatch(String rawNameFromInventory) {
        String cleanToken = QueryUtils.cleanSearchToken(rawNameFromInventory);
        if (cleanToken == null) {
            throw new IllegalArgumentException("Search token cannot be null or empty");
        }
        return getRepository().findMostSimilar(cleanToken)
                .orElseThrow(() -> new NotFoundException("No suitable " + getEntityClass().getSimpleName() + " found for: " + rawNameFromInventory));
    }
}
