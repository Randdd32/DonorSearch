package com.github.randdd32.donor_search_backend.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractCrudService<T, R extends JpaRepository<T, Long>> extends AbstractService<T, R> {
    protected AbstractCrudService(R repository, Class<T> entityClass) {
        super(repository, entityClass);
    }

    protected abstract void validate(T entity, Long id);

    protected abstract void updateFields(T existing, T updated);

    @Transactional
    public T create(T entity) {
        validate(entity, null);
        return repository.save(entity);
    }

    @Transactional
    public T update(Long id, T updatedEntity) {
        validate(updatedEntity, id);
        T existing = getById(id);
        updateFields(existing, updatedEntity);
        return repository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(getById(id));
    }

    protected void validateStringField(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be null or empty");
        }
    }
}
