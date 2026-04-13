package com.parksense.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException exception) {
        List<String> details = exception.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getDefaultMessage())
                .toList();

        ErrorResponse errorResponse = buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation failed", details);

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMessageNotReadableException(HttpMessageNotReadableException exception) {
        ErrorResponse errorResponse = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                List.of("Request body contains invalid or malformed data")
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(ProviderConfigurationException.class)
    public ResponseEntity<ErrorResponse> handleProviderConfigurationException(
            ProviderConfigurationException exception
    ) {
        ErrorResponse errorResponse = buildErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Parking data provider is not configured",
                List.of(exception.getMessage())
        );

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }

    @ExceptionHandler(ExternalProviderException.class)
    public ResponseEntity<ErrorResponse> handleExternalProviderException(ExternalProviderException exception) {
        ErrorResponse errorResponse = buildErrorResponse(
                HttpStatus.BAD_GATEWAY,
                "Parking data provider request failed",
                List.of(exception.getMessage())
        );

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(errorResponse);
    }

    private ErrorResponse buildErrorResponse(HttpStatus status, String error, List<String> details) {
        return new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                error,
                details
        );
    }
}
