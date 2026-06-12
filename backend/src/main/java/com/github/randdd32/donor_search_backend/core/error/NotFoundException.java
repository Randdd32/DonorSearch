package com.github.randdd32.donor_search_backend.core.error;

public class NotFoundException extends RuntimeException {
    public <T> NotFoundException(Class<T> entityClass, Object id) {
        super(String.format("%s with id [%s] not found", entityClass.getSimpleName(), id));
    }

    public <T> NotFoundException(Class<T> entityClass, String fieldName, Object fieldValue) {
        super(String.format("%s with %s [%s] not found", entityClass.getSimpleName(), fieldName, fieldValue));
    }

    public NotFoundException(String message) {
        super(message);
    }
}
