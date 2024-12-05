package com.duocuc.backend_srv.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserAlreadyExistsExceptionTest {

    @Test
    void testExceptionMessage() {
        String errorMessage = "User already exists with this email.";
        UserAlreadyExistsException exception = new UserAlreadyExistsException(errorMessage);

        // Verificar que el mensaje es el esperado
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void testExceptionInheritance() {
        UserAlreadyExistsException exception = new UserAlreadyExistsException("Test message");

        // Verificar que la excepción hereda de RuntimeException
        assertTrue(exception instanceof RuntimeException);
    }
}
