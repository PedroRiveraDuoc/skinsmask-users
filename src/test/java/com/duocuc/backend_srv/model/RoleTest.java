package com.duocuc.backend_srv.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

class RoleTest {

    @Test
    void testNoArgsConstructor() {
        // Act
        Role role = new Role();

        // Assert
        assertNull(role.getId());
        assertNull(role.getName());
        assertNull(role.getCode());
        assertNotNull(role.getUsers());
        assertTrue(role.getUsers().isEmpty());
    }

    @Test
    void testConstructorWithCode() {
        // Arrange
        String code = "ADMIN";

        // Act
        Role role = new Role(code);

        // Assert
        assertEquals(code, role.getCode());
        assertNull(role.getName());
        assertNotNull(role.getUsers());
        assertTrue(role.getUsers().isEmpty());
    }

    @Test
    void testConstructorWithAllFields() {
        // Arrange
        String name = "Administrator";
        String code = "ADMIN";

        // Act
        Role role = new Role(name, code);

        // Assert
        assertEquals(name, role.getName());
        assertEquals(code, role.getCode());
        assertNotNull(role.getUsers());
        assertTrue(role.getUsers().isEmpty());
    }

    @Test
    void testGettersAndSetters() {
        // Arrange
        Role role = new Role();
        Long id = 1L;
        String name = "User";
        String code = "USER";
        Set<User> users = new HashSet<>();

        // Act
        role.setId(id);
        role.setName(name);
        role.setCode(code);
        role.setUsers(users);

        // Assert
        assertEquals(id, role.getId());
        assertEquals(name, role.getName());
        assertEquals(code, role.getCode());
        assertEquals(users, role.getUsers());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        Role role1 = new Role("User", "USER");
        Role role2 = new Role("User", "USER");
        Role role3 = new Role("Admin", "ADMIN");

        // Assert
        assertEquals(role1, role2);
        assertNotEquals(role1, role3);
        assertEquals(role1.hashCode(), role2.hashCode());
        assertNotEquals(role1.hashCode(), role3.hashCode());
    }

    @Test
    void testToString() {
        // Arrange
        Role role = new Role("Admin", "ADMIN");
        role.setId(1L);

        // Act
        String result = role.toString();

        // Assert
        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("name='Admin'"));
        assertTrue(result.contains("code='ADMIN'"));
    }



    @Test
    void testEqualsWithSameObject() {
        // Arrange
        Role role = new Role("Admin", "ADMIN");

        // Act & Assert
        assertTrue(role.equals(role), "The same object should be equal to itself");
    }

    @Test
    void testEqualsWithNullObject() {
        // Arrange
        Role role = new Role("Admin", "ADMIN");

        // Act & Assert
        assertFalse(role.equals(null), "A non-null object should not be equal to null");
    }

    @Test
    void testEqualsWithDifferentClass() {
        // Arrange
        Role role = new Role("Admin", "ADMIN");
        String differentClassObject = "DifferentClassObject";

        // Act & Assert
        assertFalse(role.equals(differentClassObject), "Objects of different classes should not be equal");
    }

    @Test
    void testEqualsWithEqualObjects() {
        // Arrange
        Role role1 = new Role("Admin", "ADMIN");
        Role role2 = new Role("Admin", "ADMIN");

        // Act & Assert
        assertTrue(role1.equals(role2), "Objects with the same name and code should be equal");
    }

    @Test
    void testEqualsWithDifferentObjects() {
        // Arrange
        Role role1 = new Role("Admin", "ADMIN");
        Role role2 = new Role("User", "USER");

        // Act & Assert
        assertFalse(role1.equals(role2), "Objects with different name or code should not be equal");
    }

    @Test
    void testHashCodeWithEqualObjects() {
        // Arrange
        Role role1 = new Role("Admin", "ADMIN");
        Role role2 = new Role("Admin", "ADMIN");

        // Act & Assert
        assertEquals(role1.hashCode(), role2.hashCode(), "Equal objects should have the same hash code");
    }

    @Test
    void testHashCodeWithDifferentObjects() {
        // Arrange
        Role role1 = new Role("Admin", "ADMIN");
        Role role2 = new Role("User", "USER");

        // Act & Assert
        assertNotEquals(role1.hashCode(), role2.hashCode(), "Different objects should have different hash codes");
    }
}
