package com.duocuc.backend_srv.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testHandleConstraintViolationException() {
        // Mock ConstraintViolation
        ConstraintViolation<?> violation1 = mock(ConstraintViolation.class);
        jakarta.validation.Path path1 = mock(jakarta.validation.Path.class);
        when(path1.toString()).thenReturn("username");
        when(violation1.getPropertyPath()).thenReturn(path1);
        when(violation1.getMessage()).thenReturn("Username is mandatory");
    
        ConstraintViolation<?> violation2 = mock(ConstraintViolation.class);
        jakarta.validation.Path path2 = mock(jakarta.validation.Path.class);
        when(path2.toString()).thenReturn("password");
        when(violation2.getPropertyPath()).thenReturn(path2);
        when(violation2.getMessage()).thenReturn("Password is mandatory");
    
        ConstraintViolationException exception = new ConstraintViolationException(Set.of(violation1, violation2));
    
        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleConstraintViolationException(exception);
    
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("Validation failed", body.get("error"));
    
        Object detailsObj = body.get("details");
        assertTrue(detailsObj instanceof Map);
        Map<?, ?> detailsMap = (Map<?, ?>) detailsObj;
        assertEquals(2, detailsMap.size());
        assertEquals("Username is mandatory", detailsMap.get("username"));
        assertEquals("Password is mandatory", detailsMap.get("password"));
    }
    

    @Test
    void testHandleGlobalException() {
        // Mock a generic exception
        Exception exception = new Exception("Something went wrong");

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleGlobalException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("Unexpected error occurred", body.get("error"));
        assertEquals("Something went wrong", body.get("message"));
    }
}
