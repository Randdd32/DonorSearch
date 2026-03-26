package com.github.randdd32.donor_search_backend.core.error;

public class MissingContextDataException extends RuntimeException {
    public MissingContextDataException(String message) {
        super(message);
    }
}
