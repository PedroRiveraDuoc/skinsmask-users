package com.duocuc.backend_srv.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthResponseTest {

    @Test
    public void testFullConstructor() {
        // Arrange
        String token = "sampleToken";
        Long id = 1L;
        String username = "testUser";
        String email = "test@example.com";
        String message = "Authentication successful";

        // Act
        AuthResponse authResponse = new AuthResponse(token, id, username, email, message);

        // Assert
        assertEquals(token, authResponse.getToken(), "Token should match");
        assertEquals("Bearer", authResponse.getType(), "Default type should be 'Bearer'");
        assertEquals(id, authResponse.getId(), "ID should match");
        assertEquals(username, authResponse.getUsername(), "Username should match");
        assertEquals(email, authResponse.getEmail(), "Email should match");
        assertEquals(message, authResponse.getMessage(), "Message should match");
    }

    @Test
    public void testTokenOnlyConstructor() {
        // Arrange
        String token = "sampleToken";
        String message = "Authentication successful";

        // Act
        AuthResponse authResponse = new AuthResponse(token, message);

        // Assert
        assertEquals(token, authResponse.getToken(), "Token should match");
        assertEquals("Bearer", authResponse.getType(), "Default type should be 'Bearer'");
        assertNull(authResponse.getId(), "ID should be null for token-only response");
        assertNull(authResponse.getUsername(), "Username should be null for token-only response");
        assertNull(authResponse.getEmail(), "Email should be null for token-only response");
        assertEquals(message, authResponse.getMessage(), "Message should match");
    }

    @Test
    public void testSettersAndGetters() {
        // Arrange
        AuthResponse authResponse = new AuthResponse(null, null);
        String token = "newToken";
        String type = "CustomType";
        Long id = 2L;
        String username = "newUser";
        String email = "new@example.com";
        String message = "New message";

        // Act
        authResponse.setToken(token);
        authResponse.setType(type);
        authResponse.setId(id);
        authResponse.setUsername(username);
        authResponse.setEmail(email);
        authResponse.setMessage(message);

        // Assert
        assertEquals(token, authResponse.getToken(), "Token should match");
        assertEquals(type, authResponse.getType(), "Type should match");
        assertEquals(id, authResponse.getId(), "ID should match");
        assertEquals(username, authResponse.getUsername(), "Username should match");
        assertEquals(email, authResponse.getEmail(), "Email should match");
        assertEquals(message, authResponse.getMessage(), "Message should match");
    }

    @Test
    public void testDefaultValues() {
        // Arrange
        AuthResponse authResponse = new AuthResponse("tokenOnly", null);

        // Assert
        assertEquals("Bearer", authResponse.getType(), "Default type should be 'Bearer'");
        assertNull(authResponse.getId(), "ID should be null by default");
        assertNull(authResponse.getUsername(), "Username should be null by default");
        assertNull(authResponse.getEmail(), "Email should be null by default");
        assertNull(authResponse.getMessage(), "Message should be null by default");
    }
}
