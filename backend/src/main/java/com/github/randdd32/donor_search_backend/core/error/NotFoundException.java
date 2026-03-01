package com.github.randdd32.donor_search_backend.core.error;

public class NotFoundException extends RuntimeException {
    public <T> NotFoundException(Class<T> classT, Long id) {
        super(String.format("%s with id [%s] is not found or not exists", classT.getSimpleName(), id));
    }
}
