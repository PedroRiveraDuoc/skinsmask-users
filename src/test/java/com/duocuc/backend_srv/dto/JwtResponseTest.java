package com.duocuc.backend_srv.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtResponseTest {

    @Test
    void testConstructorAndGetter() {
        // Crear un objeto JwtResponse
        JwtResponse jwtResponse = new JwtResponse("sampleToken");

        // Verificar que el token se inicializa correctamente
        assertThat(jwtResponse.getToken()).isEqualTo("sampleToken");
    }

    @Test
    void testSetter() {
        // Crear un objeto JwtResponse vacío
        JwtResponse jwtResponse = new JwtResponse(null);

        // Establecer un nuevo token
        jwtResponse.setToken("newToken");

        // Verificar que el token se actualizó correctamente
        assertThat(jwtResponse.getToken()).isEqualTo("newToken");
    }
}
