package com.duocuc.backend_srv.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidUser() {
        User user = new User("validUser", "Strong@123", "valid@example.com");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.isEmpty(), "A valid user should not produce any validation errors.");
    }

    @Test
    public void testInvalidUsername_Blank() {
        User user = new User("", "Strong@123", "valid@example.com");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "A blank username should produce validation errors.");
        assertTrue(
            violations.stream().anyMatch(v -> v.getMessage().equals("Username is mandatory")),
            "The error message should indicate that the username is mandatory."
        );
    }

    @Test
    public void testInvalidEmail() {
        User user = new User("validUser", "Strong@123", "invalid-email");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "An invalid email should produce validation errors.");
        assertTrue(
            violations.stream().anyMatch(v -> v.getMessage().equals("Invalid email format")),
            "The error message should indicate that the email format is invalid."
        );
    }

    @Test
    public void testInvalidPassword_TooShort() {
        User user = new User("validUser", "Short1!", "valid@example.com");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "A password that is too short should produce validation errors.");
        assertTrue(
            violations.stream().anyMatch(v -> v.getMessage().contains("Password must be")),
            "The error message should indicate the password constraints."
        );
    }

    @Test
    public void testInvalidPassword_MissingSpecialCharacter() {
        User user = new User("validUser", "Strong1234", "valid@example.com");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "A password without special characters should produce validation errors.");
        assertTrue(
            violations.stream().anyMatch(v -> v.getMessage().contains("Password must be")),
            "The error message should indicate the password constraints."
        );
    }

    @Test
    public void testInvalidPassword_TooLong() {
        User user = new User("validUser", "ThisPasswordIsWayTooLong@123", "valid@example.com");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "A password that is too long should produce validation errors.");
        assertTrue(
            violations.stream().anyMatch(v -> v.getMessage().contains("Password must be")),
            "The error message should indicate the password constraints."
        );
    }

    @Test
    public void testInvalidPassword_NoNumbers() {
        User user = new User("validUser", "Strong@Password!", "valid@example.com");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "A password without numbers should produce validation errors.");
        assertTrue(
            violations.stream().anyMatch(v -> v.getMessage().contains("Password must be")),
            "The error message should indicate the password constraints."
        );
    }

    @Test
    public void testInvalidPassword_NoLetters() {
        User user = new User("validUser", "12345678@", "valid@example.com");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "A password without letters should produce validation errors.");
        assertTrue(
            violations.stream().anyMatch(v -> v.getMessage().contains("Password must be")),
            "The error message should indicate the password constraints."
        );
    }
}
