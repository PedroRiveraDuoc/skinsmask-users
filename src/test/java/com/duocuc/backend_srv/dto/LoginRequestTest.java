package com.duocuc.backend_srv.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class LoginRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidLoginRequest() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("valid@example.com");
        loginRequest.setPassword("ValidPassword");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);

        // Verificar que no hay errores de validación
        assertThat(violations).isEmpty();
    }

    @Test
    void testInvalidEmailFormat() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("invalid-email");
        loginRequest.setPassword("ValidPassword");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);

        // Verificar que hay un error de validación para el email
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Invalid email format");
    }

    @Test
    void testBlankEmail() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("");
        loginRequest.setPassword("ValidPassword");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);

        // Verificar que hay un error de validación para el email
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Email is required");
    }

    @Test
    void testBlankPassword() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("valid@example.com");
        loginRequest.setPassword("");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);

        // Verificar que hay un error de validación para el password
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Password is required");
    }

    @Test
    void testSettersAndGetters() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("TestPassword");

        // Verificar que los getters devuelvan los valores correctos
        assertThat(loginRequest.getEmail()).isEqualTo("test@example.com");
        assertThat(loginRequest.getPassword()).isEqualTo("TestPassword");
    }
}
