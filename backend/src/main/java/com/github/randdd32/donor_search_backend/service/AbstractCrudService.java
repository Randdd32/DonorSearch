package com.github.randdd32.donor_search_backend.service;

import com.github.randdd32.donor_search_backend.core.error.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractCrudService<T, R extends JpaRepository<T, Long>> {
    protected abstract R getRepository();
    protected abstract Class<T> getEntityClass();

    protected abstract void validate(T entity, Long id);

    protected abstract void updateFields(T existing, T updated);

    @Transactional(readOnly = true)
    public T getById(Long id) {
        return getRepository().findById(id)
                .orElseThrow(() -> new NotFoundException(getEntityClass(), id));
    }

    @Transactional
    public T create(T entity) {
        validate(entity, null);
        return getRepository().save(entity);
    }

    @Transactional
    public T update(Long id, T updatedEntity) {
        validate(updatedEntity, id);
        T existing = getById(id);
        updateFields(existing, updatedEntity);
        return getRepository().save(existing);
    }

    @Transactional
    public void delete(Long id) {
        getRepository().delete(getById(id));
    }

    protected void validateStringField(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be null or empty");
        }
    }
}
