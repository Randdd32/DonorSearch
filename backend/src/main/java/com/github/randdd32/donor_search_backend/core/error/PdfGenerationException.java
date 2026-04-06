package com.github.randdd32.donor_search_backend.core.error;

public class PdfGenerationException extends RuntimeException {
    public PdfGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
