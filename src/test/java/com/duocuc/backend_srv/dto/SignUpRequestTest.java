package com.duocuc.backend_srv.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SignUpRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidSignUpRequest() {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setUsername("validUser");
        signUpRequest.setPassword("Valid@123");
        signUpRequest.setEmail("valid@example.com");

        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(signUpRequest);

        // Verificar que no hay errores de validación
        assertThat(violations).isEmpty();
    }

    @Test
    void testInvalidUsername() {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setUsername("");
        signUpRequest.setPassword("Valid@123");
        signUpRequest.setEmail("valid@example.com");

        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(signUpRequest);

        // Verificar que hay un error de validación para el username
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Username is required");
    }

    @Test
    void testInvalidPassword() {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setUsername("validUser");
        signUpRequest.setPassword("short");
        signUpRequest.setEmail("valid@example.com");

        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(signUpRequest);

        // Verificar que hay un error de validación para el password
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Password must be 8-20 characters long, include letters, numbers, and at least one special character");
    }

    @Test
    void testInvalidEmail() {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setUsername("validUser");
        signUpRequest.setPassword("Valid@123");
        signUpRequest.setEmail("invalid-email");

        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(signUpRequest);

        // Verificar que hay un error de validación para el email
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Email should be valid");
    }

    @Test
    void testSettersAndGetters() {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setUsername("testUser");
        signUpRequest.setPassword("Test@1234");
        signUpRequest.setEmail("test@example.com");

        // Verificar que los getters devuelvan los valores correctos
        assertThat(signUpRequest.getUsername()).isEqualTo("testUser");
        assertThat(signUpRequest.getPassword()).isEqualTo("Test@1234");
        assertThat(signUpRequest.getEmail()).isEqualTo("test@example.com");
    }
}
