package com.github.randdd32.donor_search_backend.service;

public abstract class AbstractEntityService<T> {
    protected void validateStringField(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be null or empty");
        }
    }

    protected abstract void validate(T entity, Long id);
}
