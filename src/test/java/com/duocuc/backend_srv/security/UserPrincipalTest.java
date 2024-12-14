package com.duocuc.backend_srv.security;

import com.duocuc.backend_srv.model.User;
import com.duocuc.backend_srv.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the UserPrincipal class.
 */
public class UserPrincipalTest {

    @Test
    public void testCreateFromUser() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setFirstName("John");
        user.setLastName("Doe");

        Role role = new Role();
        role.setCode("ROLE_USER");

        List<Role> roles = new ArrayList<>();
        roles.add(role);
        Set<Role> roleSet = new HashSet<>(roles); // Convert List to Set
        user.setRoles(roleSet);

        // Act
        UserPrincipal userPrincipal = UserPrincipal.create(user);

        // Assert
        assertEquals(user.getId(), userPrincipal.getId());
        assertEquals(user.getUsername(), userPrincipal.getUsername());
        assertEquals(user.getEmail(), userPrincipal.getEmail());
        assertEquals(user.getPassword(), userPrincipal.getPassword());
        assertEquals(user.getFirstName(), userPrincipal.getFirstName());
        assertEquals(user.getLastName(), userPrincipal.getLastName());
        assertEquals(1, userPrincipal.getAuthorities().size());
        assertTrue(userPrincipal.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    public void testGettersAndIsMethods() {
        // Arrange
        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        UserPrincipal userPrincipal = new UserPrincipal(2L, "adminUser", "admin@example.com", "adminPass",
                "Admin", "User", authorities);

        // Assert
        assertEquals(2L, userPrincipal.getId());
        assertEquals("adminUser", userPrincipal.getUsername());
        assertEquals("admin@example.com", userPrincipal.getEmail());
        assertEquals("adminPass", userPrincipal.getPassword());
        assertEquals("Admin", userPrincipal.getFirstName());
        assertEquals("User", userPrincipal.getLastName());
        assertEquals(authorities, userPrincipal.getAuthorities());

        assertTrue(userPrincipal.isAccountNonExpired());
        assertTrue(userPrincipal.isAccountNonLocked());
        assertTrue(userPrincipal.isCredentialsNonExpired());
        assertTrue(userPrincipal.isEnabled());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UserPrincipal userPrincipal1 = new UserPrincipal(3L, "user1", "user1@example.com", "password1",
                "First1", "Last1", authorities);
        UserPrincipal userPrincipal2 = new UserPrincipal(3L, "user2", "user2@example.com", "password2",
                "First2", "Last2", authorities);
        UserPrincipal userPrincipal3 = new UserPrincipal(4L, "user3", "user3@example.com", "password3",
                "First3", "Last3", authorities);

        // Act & Assert
        assertEquals(userPrincipal1, userPrincipal2); // Same ID, equals should return true
        assertNotEquals(userPrincipal1, userPrincipal3); // Different ID, equals should return false

        assertEquals(userPrincipal1.hashCode(), userPrincipal2.hashCode());
        assertNotEquals(userPrincipal1.hashCode(), userPrincipal3.hashCode());
    }

    @Test
    public void testDefaultAccountProperties() {
        // Arrange
        UserPrincipal userPrincipal = new UserPrincipal(5L, "defaultUser", "default@example.com", "defaultPass",
                "Default", "User", new ArrayList<>());

        // Assert
        assertTrue(userPrincipal.isAccountNonExpired());
        assertTrue(userPrincipal.isAccountNonLocked());
        assertTrue(userPrincipal.isCredentialsNonExpired());
        assertTrue(userPrincipal.isEnabled());
    }


    @Test
    public void testEqualsWithSameObject() {
        // Arrange
        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UserPrincipal userPrincipal = new UserPrincipal(1L, "username", "email@example.com", "password",
                "FirstName", "LastName", authorities);

        // Act & Assert
        assertTrue(userPrincipal.equals(userPrincipal)); // Same object reference
    }

    @Test
    public void testEqualsWithNull() {
        // Arrange
        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UserPrincipal userPrincipal = new UserPrincipal(1L, "username", "email@example.com", "password",
                "FirstName", "LastName", authorities);

        // Act & Assert
        assertFalse(userPrincipal.equals(null)); // Null object comparison
    }

    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testEqualsWithDifferentClass() {
        // Arrange
        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UserPrincipal userPrincipal = new UserPrincipal(1L, "username", "email@example.com", "password",
                "FirstName", "LastName", authorities);
        String differentObject = "This is a String";

        // Act & Assert
        assertFalse(userPrincipal.equals(differentObject)); // Different class comparison
    }

    @Test
    public void testEqualsWithDifferentId() {
        // Arrange
        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UserPrincipal userPrincipal1 = new UserPrincipal(1L, "username1", "email1@example.com", "password1",
                "FirstName1", "LastName1", authorities);
        UserPrincipal userPrincipal2 = new UserPrincipal(2L, "username2", "email2@example.com", "password2",
                "FirstName2", "LastName2", authorities);

        // Act & Assert
        assertFalse(userPrincipal1.equals(userPrincipal2)); // Different ID values
    }
}
