package com.duocuc.backend_srv.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserDtoTest {

    @Test
    void testIdGetterAndSetter() {
        UserDto user = new UserDto();
        Long expectedId = 1L;
        user.setId(expectedId);
        assertEquals(expectedId, user.getId(), "El getter o setter de id no funciona correctamente.");
    }

    @Test
    void testUsernameGetterAndSetter() {
        UserDto user = new UserDto();
        String expectedUsername = "testuser";
        user.setUsername(expectedUsername);
        assertEquals(expectedUsername, user.getUsername(), "El getter o setter de username no funciona correctamente.");
    }

    @Test
    void testEmailGetterAndSetter() {
        UserDto user = new UserDto();
        String expectedEmail = "testuser@example.com";
        user.setEmail(expectedEmail);
        assertEquals(expectedEmail, user.getEmail(), "El getter o setter de email no funciona correctamente.");
    }

    @Test
    void testFirstNameGetterAndSetter() {
        UserDto user = new UserDto();
        String expectedFirstName = "John";
        user.setFirstName(expectedFirstName);
        assertEquals(expectedFirstName, user.getFirstName(), "El getter o setter de firstName no funciona correctamente.");
    }

    @Test
    void testLastNameGetterAndSetter() {
        UserDto user = new UserDto();
        String expectedLastName = "Doe";
        user.setLastName(expectedLastName);
        assertEquals(expectedLastName, user.getLastName(), "El getter o setter de lastName no funciona correctamente.");
    }
}
