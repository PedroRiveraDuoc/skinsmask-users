package com.duocuc.backend_srv.dto;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class UserProfileDtoTest {

    @Test
    void testConstructorAndGetters() {
        Long id = 1L;
        String username = "testuser";
        String email = "testuser@example.com";
        String firstName = "John";
        String lastName = "Doe";
        List<RoleDto> roles = List.of(new RoleDto(1L, "ROLE_USER"), new RoleDto(2L, "ROLE_ADMIN"));

        UserProfileDto userProfile = new UserProfileDto(id, username, email, firstName, lastName, roles);

        assertEquals(id, userProfile.getId(), "ID getter does not return the expected value.");
        assertEquals(username, userProfile.getUsername(), "Username getter does not return the expected value.");
        assertEquals(email, userProfile.getEmail(), "Email getter does not return the expected value.");
        assertEquals(firstName, userProfile.getFirstName(), "First name getter does not return the expected value.");
        assertEquals(lastName, userProfile.getLastName(), "Last name getter does not return the expected value.");
        assertEquals(roles, userProfile.getRoles(), "Roles getter does not return the expected value.");
    }

    @Test
    void testSetters() {
        UserProfileDto userProfile = new UserProfileDto(null, null, null, null, null, null);

        Long id = 1L;
        String username = "updatedUser";
        String email = "updated@example.com";
        String firstName = "UpdatedFirst";
        String lastName = "UpdatedLast";
        List<RoleDto> roles = List.of(new RoleDto(1L, "ROLE_USER"));

        userProfile.setId(id);
        userProfile.setUsername(username);
        userProfile.setEmail(email);
        userProfile.setFirstName(firstName);
        userProfile.setLastName(lastName);
        userProfile.setRoles(roles);

        assertEquals(id, userProfile.getId(), "ID setter does not set the expected value.");
        assertEquals(username, userProfile.getUsername(), "Username setter does not set the expected value.");
        assertEquals(email, userProfile.getEmail(), "Email setter does not set the expected value.");
        assertEquals(firstName, userProfile.getFirstName(), "First name setter does not set the expected value.");
        assertEquals(lastName, userProfile.getLastName(), "Last name setter does not set the expected value.");
        assertEquals(roles, userProfile.getRoles(), "Roles setter does not set the expected value.");
    }

    @Test
    void testEmptyRolesList() {
        UserProfileDto userProfile = new UserProfileDto(1L, "user", "user@example.com", "First", "Last", List.of());

        assertNotNull(userProfile.getRoles(), "Roles list should not be null.");
        assertTrue(userProfile.getRoles().isEmpty(), "Roles list should be empty.");
    }
}
