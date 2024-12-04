package com.duocuc.backend_srv.security;

import com.duocuc.backend_srv.model.Role;
import com.duocuc.backend_srv.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserPrincipalTest {

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User("testUser", "testPassword", "test@example.com");
        Role role = new Role("Admin", "ROLE_ADMIN");
        user.addRole(role);
    }

    @Test
    public void testUserPrincipalConstructor() {
        UserPrincipal userPrincipal = new UserPrincipal(user);

        assertEquals(user.getUsername(), userPrincipal.getUsername(), "UserPrincipal username should match User username.");
        assertEquals(user.getPassword(), userPrincipal.getPassword(), "UserPrincipal password should match User password.");

        Collection<? extends GrantedAuthority> authorities = userPrincipal.getAuthorities();
        assertNotNull(authorities, "Authorities should not be null.");
        assertEquals(1, authorities.size(), "Authorities size should match the number of roles.");

        GrantedAuthority authority = authorities.iterator().next();
        assertTrue(authority instanceof SimpleGrantedAuthority, "Authority should be an instance of SimpleGrantedAuthority.");
        assertEquals("Admin", authority.getAuthority(), "Authority name should match the role name.");
    }

    @Test
    public void testCreateMethod() {
        UserPrincipal userPrincipal = UserPrincipal.create(user);

        assertNotNull(userPrincipal, "UserPrincipal should not be null.");
        assertEquals(user.getUsername(), userPrincipal.getUsername(), "UserPrincipal username should match User username.");
        assertEquals(user.getPassword(), userPrincipal.getPassword(), "UserPrincipal password should match User password.");
    }

    @Test
    public void testIsAccountNonExpired() {
        UserPrincipal userPrincipal = new UserPrincipal(user);
        assertTrue(userPrincipal.isAccountNonExpired(), "Account should be non-expired.");
    }

    @Test
    public void testIsAccountNonLocked() {
        UserPrincipal userPrincipal = new UserPrincipal(user);
        assertTrue(userPrincipal.isAccountNonLocked(), "Account should be non-locked.");
    }

    @Test
    public void testIsCredentialsNonExpired() {
        UserPrincipal userPrincipal = new UserPrincipal(user);
        assertTrue(userPrincipal.isCredentialsNonExpired(), "Credentials should be non-expired.");
    }

    @Test
    public void testIsEnabled() {
        UserPrincipal userPrincipal = new UserPrincipal(user);
        assertTrue(userPrincipal.isEnabled(), "Account should be enabled.");
    }
}
