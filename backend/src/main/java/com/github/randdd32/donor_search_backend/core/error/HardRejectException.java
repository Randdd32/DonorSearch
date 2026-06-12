package com.github.randdd32.donor_search_backend.core.error;

public class HardRejectException extends RuntimeException {
    public HardRejectException(String message) {
        super(message);
    }
}
