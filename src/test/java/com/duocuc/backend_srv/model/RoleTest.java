package com.duocuc.backend_srv.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class RoleTest {

    private Role role;

    @BeforeEach
    public void setUp() {
        role = new Role("Admin", "ROLE_ADMIN");
    }

    @Test
    public void testConstructor() {
        Role newRole = new Role("User", "ROLE_USER");
        assertEquals("User", newRole.getName(), "Name should match the constructor input.");
        assertEquals("ROLE_USER", newRole.getCode(), "Code should match the constructor input.");
    }

    @Test
    public void testSettersAndGetters() {
        role.setName("Manager");
        assertEquals("Manager", role.getName(), "Setter or getter for name failed.");

        role.setCode("ROLE_MANAGER");
        assertEquals("ROLE_MANAGER", role.getCode(), "Setter or getter for code failed.");

        Set<User> users = new HashSet<>();
        User user = new User("username", "password", "email@example.com");
        users.add(user);
        role.setUsers(users);

        assertEquals(users, role.getUsers(), "Setter or getter for users failed.");
    }

    @Test
    public void testEquals_SameObject() {
        assertTrue(role.equals(role), "Role should be equal to itself.");
    }

    @Test
    public void testEquals_EquivalentObject() {
        Role equivalentRole = new Role("Admin", "ROLE_ADMIN");
        assertTrue(role.equals(equivalentRole), "Equivalent roles should be equal.");
    }

    @Test
    public void testEquals_DifferentObject() {
        Role differentRole = new Role("User", "ROLE_USER");
        assertFalse(role.equals(differentRole), "Different roles should not be equal.");
    }

    @Test
    public void testEquals_NullObject() {
        assertFalse(role.equals(null), "Role should not be equal to null.");
    }

    @Test
    public void testEquals_DifferentClass() {
        Object other = new Object();
        assertFalse(role.equals(other), "Role should not be equal to an object of a different class.");
    }

    @Test
    public void testHashCode_Consistent() {
        int initialHashCode = role.hashCode();
        assertEquals(initialHashCode, role.hashCode(), "HashCode should be consistent.");
    }

    @Test
    public void testHashCode_EqualObjects() {
        Role equivalentRole = new Role("Admin", "ROLE_ADMIN");
        assertEquals(role.hashCode(), equivalentRole.hashCode(), "Equal roles should have the same hash code.");
    }

    @Test
    public void testToString() {
        String expected = "Role{id=null, name='Admin', code='ROLE_ADMIN'}";
        assertEquals(expected, role.toString(), "toString output does not match the expected format.");
    }
}
