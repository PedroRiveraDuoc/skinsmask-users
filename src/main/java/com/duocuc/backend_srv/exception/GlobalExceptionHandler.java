package com.duocuc.backend_srv.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;


import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles validation exceptions thrown by Bean Validation (e.g., @Valid, @Pattern).
     *
     * @param ex the ConstraintViolationException
     * @return a structured response with validation error details
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String fieldName = violation.getPropertyPath().toString(); // Full path, refine if needed
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
        }
        return ResponseEntity.badRequest().body(Map.of(
            "error", "Validation failed",
            "details", errors
        ));
    }

    /**
     * Handles UserNotFoundException.
     *
     * @param ex the UserNotFoundException
     * @return a structured response with error details
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFoundException(UserNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

    /**
     * Handles UsernameAlreadyExistsException.
     *
     * @param ex the UsernameAlreadyExistsException
     * @return a structured response with error details
     */
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
    }

    /**
     * Handles EmailAlreadyExistsException.
     *
     * @param ex the EmailAlreadyExistsException
     * @return a structured response with error details
     */
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
    }

    /**
     * Handles all uncaught exceptions globally to ensure a consistent error response.
     *
     * @param ex the Exception
     * @return a structured response with the error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
            "error", "Unexpected error occurred",
            "message", ex.getMessage()
        ));
    }
}
