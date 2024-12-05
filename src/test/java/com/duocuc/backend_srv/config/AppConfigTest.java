package com.duocuc.backend_srv.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AppConfigTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void testPasswordEncoderBeanIsLoaded() {
        // Verificar que el bean se cargó correctamente
        assertThat(passwordEncoder).isNotNull();
    }

    @Test
    void testPasswordEncoderFunctionality() {
        String rawPassword = "testPassword";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // Verificar que la contraseña no es igual al texto plano
        assertThat(encodedPassword).isNotEqualTo(rawPassword);

        // Verificar que la contraseña cifrada coincide con el texto plano
        assertThat(passwordEncoder.matches(rawPassword, encodedPassword)).isTrue();
    }

    @Test
    void testPasswordEncoderDoesNotMatchIncorrectPassword() {
        String rawPassword = "testPassword";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // Verificar que una contraseña incorrecta no coincide
        assertThat(passwordEncoder.matches("wrongPassword", encodedPassword)).isFalse();
    }
}
