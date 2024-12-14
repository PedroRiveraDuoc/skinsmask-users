package com.duocuc.backend_srv.dto;

import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SignUpRequestTest {

    private final Validator validator;

    public SignUpRequestTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    void testValidSignUpRequest() {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("valid_user");
        request.setPassword("ValidPass123!");
        request.setEmail("valid@example.com");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setRoles(Set.of("USER"));

        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty(), "There should be no validation errors for a valid request.");
    }

    @Test
    void testUsernameValidation() {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("in");
        request.setPassword("ValidPass123!");
        request.setEmail("valid@example.com");
        request.setFirstName("John");
        request.setLastName("Doe");

        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty(), "Validation should fail for an invalid username.");
        assertTrue(
                violations.stream().anyMatch(v -> v.getMessage().contains("Username must be between 3 and 20 characters")),
                "Error message should indicate the username length requirement."
        );
    }

    @Test
    void testPasswordValidation() {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("valid_user");
        request.setPassword("invalid");
        request.setEmail("valid@example.com");
        request.setFirstName("John");
        request.setLastName("Doe");

        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty(), "Validation should fail for an invalid password.");
        assertTrue(
                violations.stream().anyMatch(v -> v.getMessage().contains("Password must be 8-20 characters long")),
                "Error message should indicate the password format requirement."
        );
    }

    @Test
    void testEmailValidation() {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("valid_user");
        request.setPassword("ValidPass123!");
        request.setEmail("invalid-email");
        request.setFirstName("John");
        request.setLastName("Doe");

        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty(), "Validation should fail for an invalid email.");
        assertTrue(
                violations.stream().anyMatch(v -> v.getMessage().contains("Email should be valid")),
                "Error message should indicate the email format requirement."
        );
    }

    @Test
    void testFirstNameValidation() {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("valid_user");
        request.setPassword("ValidPass123!");
        request.setEmail("valid@example.com");
        request.setFirstName("J");
        request.setLastName("Doe");

        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty(), "Validation should fail for an invalid first name.");
        assertTrue(
                violations.stream().anyMatch(v -> v.getMessage().contains("First name must be between 2 and 50 characters")),
                "Error message should indicate the first name length requirement."
        );
    }

    @Test
    void testLastNameValidation() {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("valid_user");
        request.setPassword("ValidPass123!");
        request.setEmail("valid@example.com");
        request.setFirstName("John");
        request.setLastName("");

        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty(), "Validation should fail for an invalid last name.");
        assertTrue(
                violations.stream().anyMatch(v -> v.getMessage().contains("Last name is required")),
                "Error message should indicate the last name is required."
        );
    }

    @Test
    void testRolesSetterAndGetter() {
        SignUpRequest request = new SignUpRequest();
        Set<String> roles = Set.of("USER", "ADMIN");

        request.setRoles(roles);

        assertEquals(roles, request.getRoles(), "Roles getter should return the roles set by the setter.");
    }
}
