package com.github.randdd32.donor_search_backend.core.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDetails> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        return buildClientError("INVALID_ARGUMENT", ex.getMessage(), request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorDetails> handleNotFoundException(NotFoundException ex, WebRequest request) {
        return buildClientError("RESOURCE_NOT_FOUND", ex.getMessage(), request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorDetails> handleNoResourceFoundException(NoResourceFoundException ex, WebRequest request) {
        return buildClientError("ENDPOINT_NOT_FOUND", ex.getMessage(), request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDetails> HttpMessageNotReadableException(HttpMessageNotReadableException ex, WebRequest request) {
        return buildClientError("MALFORMED_JSON_OR_TYPE_MISMATCH",
                "Malformed JSON request or invalid data types (e.g., invalid enum value)", request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorDetails> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex, WebRequest request) {
        String message = String.format("Parameter '%s' should be of type '%s'",
                ex.getName(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "Unknown");
        return buildClientError("TYPE_MISMATCH", message, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorDetails> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex, WebRequest request) {
        String message = String.format("Required query parameter '%s' is missing", ex.getParameterName());
        return buildClientError("MISSING_PARAMETER", message, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorDetails> DataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request) {
        return buildClientError("DATA_INTEGRITY_VIOLATION", "Database constraint violation (e.g., duplicate unique key)",
                request, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorDetails> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex, WebRequest request) {
        return buildClientError("METHOD_NOT_ALLOWED", ex.getMessage(), request, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetails> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> Optional.ofNullable(error.getDefaultMessage()).orElse("Invalid value"),
                        (existing, replacement) -> existing + ", " + replacement
                ));

        ErrorDetails errorDetails = new ErrorDetails(
                "DTO_VALIDATION_FAILED",
                "Validation failed for one or more fields",
                request.getDescription(false),
                errors
        );

        log.warn("Validation failed [{}]: {}", request.getDescription(false), errors);
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorDetails> handleIllegalStateException(IllegalStateException ex, WebRequest request) {
        return buildServerError("ILLEGAL_STATE", ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(Exception ex, WebRequest request) {
        return buildServerError("INTERNAL_ERROR", ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorDetails> buildClientError(String errorCode, String message, WebRequest request, HttpStatus status) {
        ErrorDetails errorDetails = new ErrorDetails(errorCode, message, request.getDescription(false));
        log.warn("Client error [{}] at {}: {}", errorCode, request.getDescription(false), message);
        return new ResponseEntity<>(errorDetails, status);
    }

    private ResponseEntity<ErrorDetails> buildServerError(String errorCode, Exception ex, WebRequest request, HttpStatus status) {
        ErrorDetails errorDetails = new ErrorDetails(
                errorCode,
                "An unexpected internal server error occurred",
                request.getDescription(false)
        );
        log.error("Server error [{}] occurred at {}: {}", errorCode, request.getDescription(false), ex.getMessage(), ex);
        return new ResponseEntity<>(errorDetails, status);
    }
}
