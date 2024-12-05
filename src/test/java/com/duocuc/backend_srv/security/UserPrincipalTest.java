package com.duocuc.backend_srv.security;

import com.duocuc.backend_srv.model.Role;
import com.duocuc.backend_srv.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserPrincipalTest {

    private User user;
    private UserPrincipal userPrincipal;

    @BeforeEach
    void setUp() {
        // Create roles
        Role roleUser = new Role();
        roleUser.setId(1L);
        roleUser.setName("ROLE_USER");

        Role roleAdmin = new Role();
        roleAdmin.setId(2L);
        roleAdmin.setName("ROLE_ADMIN");

        // Create user
        user = new User();
        user.setId(123L);
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setPassword("password123");
        user.setRoles(Set.of(roleUser, roleAdmin));

        // Create UserPrincipal from user
        userPrincipal = UserPrincipal.create(user);
    }

    @Test
    void testCreateFromUser() {
        UserPrincipal createdUserPrincipal = UserPrincipal.create(user);

        assertEquals(user.getId(), createdUserPrincipal.getId());
        assertEquals(user.getUsername(), createdUserPrincipal.getUsername());
        assertEquals(user.getEmail(), createdUserPrincipal.getEmail());
        assertEquals(user.getPassword(), createdUserPrincipal.getPassword());

        List<String> expectedAuthorities = Arrays.asList("ROLE_USER", "ROLE_ADMIN");
        List<String> actualAuthorities = createdUserPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        assertTrue(actualAuthorities.containsAll(expectedAuthorities));
    }

    @Test
    void testGetId() {
        assertEquals(123L, userPrincipal.getId());
    }

    @Test
    void testGetUsername() {
        assertEquals("testuser", userPrincipal.getUsername());
    }

    @Test
    void testGetEmail() {
        assertEquals("testuser@example.com", userPrincipal.getEmail());
    }

    @Test
    void testGetPassword() {
        assertEquals("password123", userPrincipal.getPassword());
    }

    @Test
    void testGetAuthorities() {
        Collection<? extends GrantedAuthority> authorities = userPrincipal.getAuthorities();

        assertEquals(2, authorities.size());
        assertTrue(authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
        assertTrue(authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void testIsAccountNonExpired() {
        assertTrue(userPrincipal.isAccountNonExpired());
    }

    @Test
    void testIsAccountNonLocked() {
        assertTrue(userPrincipal.isAccountNonLocked());
    }

    @Test
    void testIsCredentialsNonExpired() {
        assertTrue(userPrincipal.isCredentialsNonExpired());
    }

    @Test
    void testIsEnabled() {
        assertTrue(userPrincipal.isEnabled());
    }

    @Test
    void testEqualsAndHashCode() {
        UserPrincipal sameUserPrincipal = UserPrincipal.create(user);

        assertEquals(userPrincipal, sameUserPrincipal);
        assertEquals(userPrincipal.hashCode(), sameUserPrincipal.hashCode());

        User differentUser = new User();
        differentUser.setId(456L);
        differentUser.setUsername("differentuser");
        differentUser.setEmail("different@example.com");
        differentUser.setPassword("password456");
        differentUser.setRoles(Set.of());

        UserPrincipal differentUserPrincipal = UserPrincipal.create(differentUser);

        assertNotEquals(userPrincipal, differentUserPrincipal);
        assertNotEquals(userPrincipal.hashCode(), differentUserPrincipal.hashCode());
    }
}
