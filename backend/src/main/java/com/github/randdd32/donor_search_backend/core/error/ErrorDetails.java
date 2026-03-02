package com.github.randdd32.donor_search_backend.core.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorDetails(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
        Instant timestamp,
        String error,
        String message,
        String details,
        Map<String, String> fieldErrors
) {
    public ErrorDetails(String error, String message, String details) {
        this(Instant.now(), error, message, details, null);
    }

    public ErrorDetails(String error, String message, String details, Map<String, String> fieldErrors) {
        this(Instant.now(), error, message, details, fieldErrors);
    }
}
