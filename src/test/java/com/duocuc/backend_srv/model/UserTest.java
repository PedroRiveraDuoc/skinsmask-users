package com.duocuc.backend_srv.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testUserCreation_Success() {
        User user = new User("testuser", "Valid@1234", "testuser@example.com");
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(0, violations.size());
        assertEquals("testuser", user.getUsername());
        assertEquals("Valid@1234", user.getPassword());
        assertEquals("testuser@example.com", user.getEmail());
    }

    @Test
    void testUserCreation_Failure_UsernameBlank() {
        User user = new User("", "Valid@1234", "testuser@example.com");
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Username is mandatory")));
    }

    @Test
    void testUserCreation_Failure_PasswordBlank() {
        User user = new User("testuser", "", "testuser@example.com");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
    
        // Verifica que haya 2 violaciones
        assertEquals(2, violations.size());
    
        // Verifica que una de las violaciones sea por el campo en blanco
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Password is mandatory")));
    
        // Verifica que otra violación sea por el patrón
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Password must be 8-20 characters long, include letters, numbers, and at least one special character")));
    }

    @Test
    void testUserCreation_Failure_PasswordInvalid() {
        User user = new User("testuser", "password", "testuser@example.com");
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Password must be 8-20 characters long")));
    }

    @Test
    void testUserCreation_Failure_EmailInvalid() {
        User user = new User("testuser", "Valid@1234", "invalid-email");
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Invalid email format")));
    }

    @Test
    void testUserRoles_AddRole() {
        User user = new User("testuser", "Valid@1234", "testuser@example.com");
        Role role = new Role();
        role.setName("ROLE_USER");

        user.addRole(role);

        assertEquals(1, user.getRoles().size());
        assertTrue(user.getRoles().contains(role));
    }

    @Test
    void testUserRoles_RemoveRole() {
        User user = new User("testuser", "Valid@1234", "testuser@example.com");
        Role role = new Role();
        role.setName("ROLE_USER");

        user.addRole(role);
        assertEquals(1, user.getRoles().size());

        user.removeRole(role);
        assertEquals(0, user.getRoles().size());
    }

    @Test
    void testUserSettersAndGetters() {
        User user = new User();

        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("Valid@1234");
        user.setEmail("testuser@example.com");

        assertEquals(1L, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("Valid@1234", user.getPassword());
        assertEquals("testuser@example.com", user.getEmail());
    }

    @Test
    void testUserToString() {
        User user = new User("testuser", "Valid@1234", "testuser@example.com");
        String expectedToString = "User{id=null, username='testuser', email='testuser@example.com', roles=[]}";

        assertEquals(expectedToString, user.toString());
    }

    @Test
    void testUserWithMultipleRoles() {
        User user = new User("testuser", "Valid@1234", "testuser@example.com");

        Role role1 = new Role();
        role1.setName("ROLE_USER");

        Role role2 = new Role();
        role2.setName("ROLE_ADMIN");

        user.addRole(role1);
        user.addRole(role2);

        assertEquals(2, user.getRoles().size());
        assertTrue(user.getRoles().contains(role1));
        assertTrue(user.getRoles().contains(role2));
    }

    @Test
    void testUserIdNotSetInitially() {
        User user = new User("testuser", "Valid@1234", "testuser@example.com");
        assertNull(user.getId());
    }
}
