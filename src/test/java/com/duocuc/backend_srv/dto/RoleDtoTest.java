package com.duocuc.backend_srv.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RoleDtoTest {

    @Test
    void testConstructorAndGetters() {
        // Crear un objeto RoleDto
        RoleDto role = new RoleDto(1L, "ROLE_ADMIN");

        // Verificar los valores iniciales
        assertThat(role.getId()).isEqualTo(1L);
        assertThat(role.getName()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void testSetters() {
        // Crear un objeto RoleDto vacío
        RoleDto role = new RoleDto(null, null);

        // Establecer nuevos valores
        role.setId(2L);
        role.setName("ROLE_USER");

        // Verificar que los valores se actualizaron correctamente
        assertThat(role.getId()).isEqualTo(2L);
        assertThat(role.getName()).isEqualTo("ROLE_USER");
    }
}
