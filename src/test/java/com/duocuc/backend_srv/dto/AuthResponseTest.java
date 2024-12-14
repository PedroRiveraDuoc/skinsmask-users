package com.duocuc.backend_srv.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the AuthResponse class.
 */
public class AuthResponseTest {

    @Test
    public void testFullConstructor() {
        // Arrange
        String token = "sampleToken";
        Long id = 123L;
        String username = "testUser";
        String email = "test@example.com";
        String firstName = "John";
        String lastName = "Doe";
        String message = "Success";

        // Act
        AuthResponse authResponse = new AuthResponse(token, id, username, email, firstName, lastName, message);

        // Assert
        assertEquals(token, authResponse.getToken());
        assertEquals(id, authResponse.getId());
        assertEquals(username, authResponse.getUsername());
        assertEquals(email, authResponse.getEmail());
        assertEquals(firstName, authResponse.getFirstName());
        assertEquals(lastName, authResponse.getLastName());
        assertEquals(message, authResponse.getMessage());
        assertEquals("Bearer", authResponse.getType());
    }

    @Test
    public void testTokenOnlyConstructor() {
        // Arrange
        String token = "sampleToken";
        String message = "Partial Success";

        // Act
        AuthResponse authResponse = new AuthResponse(token, message);

        // Assert
        assertEquals(token, authResponse.getToken());
        assertEquals(message, authResponse.getMessage());
        assertEquals("Bearer", authResponse.getType());
        assertNull(authResponse.getId());
        assertNull(authResponse.getUsername());
        assertNull(authResponse.getEmail());
        assertNull(authResponse.getFirstName());
        assertNull(authResponse.getLastName());
    }

    @Test
    public void testSettersAndGetters() {
        // Arrange
        AuthResponse authResponse = new AuthResponse("initialToken", "Initial Message");

        // Act
        authResponse.setToken("newToken");
        authResponse.setType("CustomType");
        authResponse.setId(456L);
        authResponse.setUsername("newUser");
        authResponse.setEmail("new@example.com");
        authResponse.setFirstName("Jane");
        authResponse.setLastName("Smith");
        authResponse.setMessage("Updated Message");

        // Assert
        assertEquals("newToken", authResponse.getToken());
        assertEquals("CustomType", authResponse.getType());
        assertEquals(456L, authResponse.getId());
        assertEquals("newUser", authResponse.getUsername());
        assertEquals("new@example.com", authResponse.getEmail());
        assertEquals("Jane", authResponse.getFirstName());
        assertEquals("Smith", authResponse.getLastName());
        assertEquals("Updated Message", authResponse.getMessage());
    }
}
