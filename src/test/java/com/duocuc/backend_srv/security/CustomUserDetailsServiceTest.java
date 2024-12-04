package com.duocuc.backend_srv.security;

import com.duocuc.backend_srv.model.Role;
import com.duocuc.backend_srv.model.User;
import com.duocuc.backend_srv.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CustomUserDetailsServiceTest {

    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        customUserDetailsService = new CustomUserDetailsService(userRepository);
    }

    @Test
    public void testLoadUserByUsername_UserExists() {
        // Arrange
        Role role = new Role("Admin", "ROLE_ADMIN");
        User user = new User("testUser", "testPassword", "test@example.com");
        user.setRoles(Set.of(role));

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("test@example.com");

        // Assert
        assertNotNull(userDetails, "UserDetails should not be null.");
        assertEquals("test@example.com", userDetails.getUsername(), "Username should match.");
        assertEquals("testPassword", userDetails.getPassword(), "Password should match.");
        assertTrue(
                userDetails.getAuthorities().stream()
                        .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")),
                "Authorities should include ROLE_ADMIN.");

        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    public void testLoadUserByUsername_UserNotFound() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("nonexistent@example.com"),
                "Expected UsernameNotFoundException to be thrown.");

        assertEquals("User not found with email: nonexistent@example.com", exception.getMessage(),
                "Exception message should match.");
        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
    }

    @Test
    public void testLoadUserByUsername_UserWithMultipleRoles() {
        // Arrange
        Role adminRole = new Role("Admin", "ROLE_ADMIN");
        Role userRole = new Role("User", "ROLE_USER");
        User user = new User("testUser", "testPassword", "test@example.com");
        user.setRoles(Set.of(adminRole, userRole));

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("test@example.com");

        // Assert
        assertNotNull(userDetails, "UserDetails should not be null.");
        assertEquals("test@example.com", userDetails.getUsername(), "Username should match.");
        assertEquals("testPassword", userDetails.getPassword(), "Password should match.");
        assertTrue(
                userDetails.getAuthorities().stream()
                        .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")),
                "Authorities should include ROLE_ADMIN.");
        assertTrue(
                userDetails.getAuthorities().stream()
                        .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")),
                "Authorities should include ROLE_USER.");

        verify(userRepository, times(1)).findByEmail("test@example.com");
    }
}
