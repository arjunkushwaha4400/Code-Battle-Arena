package com.arena.common.exception;

import com.arena.common.dto.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<Void>> handleBaseException(BaseException ex) {
        log.error("Base exception occurred: {}", ex.getMessage());

        ApiResponse<Void> response = ApiResponse.error(
                ex.getMessage(),
                ApiResponse.ErrorDetails.builder()
                        .code(ex.getErrorCode())
                        .description(ex.getMessage())
                        .build()
        );

        return new ResponseEntity<>(response, ex.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.error("Validation failed: {}", errors);

        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .success(false)
                .message("Validation failed")
                .data(errors)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(
            ConstraintViolationException ex) {

        log.error("Constraint violation: {}", ex.getMessage());

        ApiResponse<Void> response = ApiResponse.error(
                "Validation failed: " + ex.getMessage(),
                ApiResponse.ErrorDetails.builder()
                        .code("CONSTRAINT_VIOLATION")
                        .description(ex.getMessage())
                        .build()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);

        ApiResponse<Void> response = ApiResponse.error(
                "An unexpected error occurred. Please try again later.",
                ApiResponse.ErrorDetails.builder()
                        .code("INTERNAL_ERROR")
                        .description(ex.getMessage())
                        .build()
        );

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}