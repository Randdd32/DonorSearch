package com.github.randdd32.donor_search_backend.service;

import com.github.randdd32.donor_search_backend.core.error.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractService<T, R extends JpaRepository<T, Long>> {
    protected abstract R getRepository();
    protected abstract Class<T> getEntityClass();

    @Transactional(readOnly = true)
    public T getById(Long id) {
        return getRepository().findById(id)
                .orElseThrow(() -> new NotFoundException(getEntityClass(), id));
    }
}
