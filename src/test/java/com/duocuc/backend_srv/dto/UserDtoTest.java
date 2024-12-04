package com.duocuc.backend_srv.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserDtoTest {

    @Test
    public void testGettersAndSetters() {
        // Arrange
        UserDto userDto = new UserDto();
        Long id = 1L;
        String username = "testUser";
        String email = "test@example.com";

        // Act
        userDto.setId(id);
        userDto.setUsername(username);
        userDto.setEmail(email);

        // Assert
        assertEquals(id, userDto.getId(), "ID should match the set value.");
        assertEquals(username, userDto.getUsername(), "Username should match the set value.");
        assertEquals(email, userDto.getEmail(), "Email should match the set value.");
    }

    @Test
    public void testDefaultValues() {
        // Arrange
        UserDto userDto = new UserDto();

        // Assert
        assertNull(userDto.getId(), "Default ID should be null.");
        assertNull(userDto.getUsername(), "Default username should be null.");
        assertNull(userDto.getEmail(), "Default email should be null.");
    }

    @Test
    public void testPartialSetters() {
        // Arrange
        UserDto userDto = new UserDto();
        String username = "partialUser";

        // Act
        userDto.setUsername(username);

        // Assert
        assertNull(userDto.getId(), "ID should remain null.");
        assertEquals(username, userDto.getUsername(), "Username should match the set value.");
        assertNull(userDto.getEmail(), "Email should remain null.");
    }

    @Test
    public void testSetNullValues() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setId(2L);
        userDto.setUsername("existingUser");
        userDto.setEmail("existing@example.com");

        // Act
        userDto.setId(null);
        userDto.setUsername(null);
        userDto.setEmail(null);

        // Assert
        assertNull(userDto.getId(), "ID should be null after being set to null.");
        assertNull(userDto.getUsername(), "Username should be null after being set to null.");
        assertNull(userDto.getEmail(), "Email should be null after being set to null.");
    }
}
