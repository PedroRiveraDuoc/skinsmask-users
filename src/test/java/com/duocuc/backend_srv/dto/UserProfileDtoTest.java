package com.duocuc.backend_srv.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserProfileDtoTest {

    @Test
    void testConstructorAndGetters() {
        // Crear una lista de roles
        RoleDto role1 = new RoleDto(1L, "ROLE_ADMIN");
        RoleDto role2 = new RoleDto(2L, "ROLE_USER");
        List<RoleDto> roles = List.of(role1, role2);

        // Crear el UserProfileDto
        UserProfileDto userProfile = new UserProfileDto(1L, "testUser", roles);

        // Verificar valores iniciales
        assertThat(userProfile.getId()).isEqualTo(1L);
        assertThat(userProfile.getUsername()).isEqualTo("testUser");
        assertThat(userProfile.getRoles()).isEqualTo(roles);
    }

    @Test
    void testSetters() {
        // Crear un UserProfileDto vacío
        UserProfileDto userProfile = new UserProfileDto(null, null, null);

        // Crear nuevos valores
        List<RoleDto> newRoles = List.of(new RoleDto(3L, "ROLE_MANAGER"));
        userProfile.setId(2L);
        userProfile.setUsername("newUser");
        userProfile.setRoles(newRoles);

        // Verificar valores actualizados
        assertThat(userProfile.getId()).isEqualTo(2L);
        assertThat(userProfile.getUsername()).isEqualTo("newUser");
        assertThat(userProfile.getRoles()).isEqualTo(newRoles);
    }
}
