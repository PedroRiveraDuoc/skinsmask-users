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

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

        private CustomUserDetailsService customUserDetailsService;

        @Mock
        private UserRepository userRepository;

        @BeforeEach
        void setUp() {
                MockitoAnnotations.openMocks(this);
                customUserDetailsService = new CustomUserDetailsService(userRepository);
        }

        @Test
        void testLoadUserByUsername_UserNotFound() {
                // Arrange
                when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

                // Act & Assert
                assertThrows(UsernameNotFoundException.class,
                                () -> customUserDetailsService.loadUserByUsername("nonexistent@example.com"));
        }

}