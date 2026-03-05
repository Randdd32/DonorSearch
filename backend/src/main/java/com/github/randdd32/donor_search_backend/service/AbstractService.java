package com.github.randdd32.donor_search_backend.service;

import com.github.randdd32.donor_search_backend.core.error.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractService<T, R extends JpaRepository<T, Long>> {
    protected final R repository;
    protected final Class<T> entityClass;

    protected AbstractService(R repository, Class<T> entityClass) {
        this.repository = repository;
        this.entityClass = entityClass;
    }

    @Transactional(readOnly = true)
    public T getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityClass, id));
    }
}
