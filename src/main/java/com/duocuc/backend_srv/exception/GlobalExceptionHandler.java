package com.duocuc.backend_srv.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  // Manejo de errores generales
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleGlobalException(Exception ex, WebRequest request) {
    logger.error("Unexpected error occurred: {}", ex.getMessage(), ex);

    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("message", "An unexpected error occurred");
    body.put("details", ex.getMessage());
    body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());

    return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  // Manejo de excepciones personalizadas (si las tienes)
  @ExceptionHandler(UserAlreadyExistsException.class)
  public ResponseEntity<Object> handleUserNotFoundException(UserAlreadyExistsException ex, WebRequest request) {
    logger.warn("User not found: {}", ex.getMessage(), ex);

    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("message", ex.getMessage());
    body.put("status", HttpStatus.NOT_FOUND.value());

    return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
  }
}
