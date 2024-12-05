package com.duocuc.backend_srv.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthResponseTest {

    @Test
    void testFullConstructorAndGetters() {
        // Crear un objeto AuthResponse con el constructor completo
        AuthResponse authResponse = new AuthResponse(
                "sampleToken", 
                1L, 
                "testUser", 
                "test@example.com", 
                "Success");

        // Verificar los valores iniciales
        assertThat(authResponse.getToken()).isEqualTo("sampleToken");
        assertThat(authResponse.getId()).isEqualTo(1L);
        assertThat(authResponse.getUsername()).isEqualTo("testUser");
        assertThat(authResponse.getEmail()).isEqualTo("test@example.com");
        assertThat(authResponse.getMessage()).isEqualTo("Success");
        assertThat(authResponse.getType()).isEqualTo("Bearer");
    }

    @Test
    void testTokenOnlyConstructorAndGetters() {
        // Crear un objeto AuthResponse con el constructor de token y mensaje
        AuthResponse authResponse = new AuthResponse("sampleToken", "Success");

        // Verificar los valores iniciales
        assertThat(authResponse.getToken()).isEqualTo("sampleToken");
        assertThat(authResponse.getMessage()).isEqualTo("Success");
        assertThat(authResponse.getId()).isNull();
        assertThat(authResponse.getUsername()).isNull();
        assertThat(authResponse.getEmail()).isNull();
        assertThat(authResponse.getType()).isEqualTo("Bearer");
    }

    @Test
    void testSetters() {
        // Crear un objeto AuthResponse vacío
        AuthResponse authResponse = new AuthResponse("initialToken", "Initial message");

        // Establecer nuevos valores
        authResponse.setToken("newToken");
        authResponse.setId(2L);
        authResponse.setUsername("newUser");
        authResponse.setEmail("new@example.com");
        authResponse.setMessage("Updated message");
        authResponse.setType("UpdatedType");

        // Verificar que los valores se actualizaron correctamente
        assertThat(authResponse.getToken()).isEqualTo("newToken");
        assertThat(authResponse.getId()).isEqualTo(2L);
        assertThat(authResponse.getUsername()).isEqualTo("newUser");
        assertThat(authResponse.getEmail()).isEqualTo("new@example.com");
        assertThat(authResponse.getMessage()).isEqualTo("Updated message");
        assertThat(authResponse.getType()).isEqualTo("UpdatedType");
    }
}
