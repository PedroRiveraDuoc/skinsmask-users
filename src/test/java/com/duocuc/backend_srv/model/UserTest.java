package com.duocuc.backend_srv.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;
    private Role roleAdmin;
    private Role roleUser;

    @BeforeEach
    void setUp() {
        // Inicializar la instancia de User antes de cada prueba
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEmail("testuser@example.com");

        // Crear roles de ejemplo
        roleAdmin = new Role();
        roleAdmin.setId(1L);
        roleAdmin.setName("ROLE_ADMIN");

        roleUser = new Role();
        roleUser.setId(2L);
        roleUser.setName("ROLE_USER");

        // Asignar roles al usuario
        Set<Role> roles = new HashSet<>();
        roles.add(roleAdmin);
        user.setRoles(roles);
    }

    @Test
    void testNoArgsConstructor() {
        User emptyUser = new User();
        assertNotNull(emptyUser);
        assertNull(emptyUser.getId());
        assertNull(emptyUser.getUsername());
        assertNull(emptyUser.getPassword());
        assertNull(emptyUser.getEmail());
        assertNotNull(emptyUser.getRoles());
        assertTrue(emptyUser.getRoles().isEmpty());
    }

    @Test
    void testAllArgsConstructor() {
        User userWithArgs = new User("user1", "securePassword", "user1@example.com");

        assertNotNull(userWithArgs);
        assertEquals("user1", userWithArgs.getUsername());
        assertEquals("securePassword", userWithArgs.getPassword());
        assertEquals("user1@example.com", userWithArgs.getEmail());
        assertNotNull(userWithArgs.getRoles());
        assertTrue(userWithArgs.getRoles().isEmpty());
    }

    @Test
    void testGettersAndSetters() {
        user.setId(2L);
        user.setUsername("updatedUser");
        user.setPassword("updatedPassword");
        user.setEmail("updated@example.com");

        assertEquals(2L, user.getId());
        assertEquals("updatedUser", user.getUsername());
        assertEquals("updatedPassword", user.getPassword());
        assertEquals("updated@example.com", user.getEmail());
    }

    @Test
    void testAddRole() {
        user.addRole(roleUser);
        assertEquals(2, user.getRoles().size());
        assertTrue(user.getRoles().contains(roleUser));
    }

    @Test
    void testRemoveRole() {
        user.removeRole(roleAdmin);
        assertTrue(user.getRoles().isEmpty());
    }

    @Test
    void testToString() {
        String userString = user.toString();
        assertNotNull(userString);
        assertTrue(userString.contains("testuser"));
        assertTrue(userString.contains("testuser@example.com"));
        assertTrue(userString.contains("ROLE_ADMIN"));
    }

    @Test
    void testRolesAreMutable() {
        Set<Role> roles = user.getRoles();
        assertNotNull(roles);

        roles.add(roleUser);
        assertTrue(user.getRoles().contains(roleUser));
    }
}
