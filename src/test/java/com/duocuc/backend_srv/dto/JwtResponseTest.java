package com.duocuc.backend_srv.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the JwtResponse class.
 */
public class JwtResponseTest {

    @Test
    public void testConstructor() {
        // Arrange
        String token = "sampleJwtToken";
        Long id = 101L;
        String username = "testUser";
        String email = "user@example.com";
        String firstName = "John";
        String lastName = "Doe";

        // Act
        JwtResponse jwtResponse = new JwtResponse(token, id, username, email, firstName, lastName);

        // Assert
        assertEquals(token, jwtResponse.getToken());
        assertEquals(id, jwtResponse.getId());
        assertEquals(username, jwtResponse.getUsername());
        assertEquals(email, jwtResponse.getEmail());
        assertEquals(firstName, jwtResponse.getFirstName());
        assertEquals(lastName, jwtResponse.getLastName());
        assertEquals("Bearer", jwtResponse.getType());
    }

    @Test
    public void testSettersAndGetters() {
        // Arrange
        JwtResponse jwtResponse = new JwtResponse("initialToken", 200L, "initialUser", "initial@example.com", "Initial", "User");

        // Act
        jwtResponse.setToken("newToken");
        jwtResponse.setType("CustomType");
        jwtResponse.setId(202L);
        jwtResponse.setUsername("updatedUser");
        jwtResponse.setEmail("updated@example.com");
        jwtResponse.setFirstName("UpdatedFirst");
        jwtResponse.setLastName("UpdatedLast");

        // Assert
        assertEquals("newToken", jwtResponse.getToken());
        assertEquals("CustomType", jwtResponse.getType());
        assertEquals(202L, jwtResponse.getId());
        assertEquals("updatedUser", jwtResponse.getUsername());
        assertEquals("updated@example.com", jwtResponse.getEmail());
        assertEquals("UpdatedFirst", jwtResponse.getFirstName());
        assertEquals("UpdatedLast", jwtResponse.getLastName());
    }
}
