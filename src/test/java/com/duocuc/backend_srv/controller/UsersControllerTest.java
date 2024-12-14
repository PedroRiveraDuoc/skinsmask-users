package com.duocuc.backend_srv.controller;

import com.duocuc.backend_srv.dto.SignUpRequest;
import com.duocuc.backend_srv.dto.UserProfileDto;
import com.duocuc.backend_srv.exception.EmailAlreadyExistsException;
import com.duocuc.backend_srv.exception.UserNotFoundException;
import com.duocuc.backend_srv.model.User;
import com.duocuc.backend_srv.service.UserService;
import com.duocuc.backend_srv.util.JwtUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class UsersControllerTest {

    private UserService userService;
    private JwtUtils jwtUtils;
    private UsersController usersController;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        jwtUtils = mock(JwtUtils.class);
        usersController = new UsersController(userService, jwtUtils);
    }

    @Test
    void testGetAuthenticatedUserProfile_Success() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        String token = "validToken";

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setEmail("testuser@example.com");
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        mockUser.setRoles(new HashSet<>());

        when(jwtUtils.getJwtFromRequest(request)).thenReturn(token);
        when(userService.getAuthenticatedUser(token)).thenReturn(Optional.of(mockUser));

        ResponseEntity<?> response = usersController.getAuthenticatedUserProfile(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(UserProfileDto.class, response.getBody().getClass());
        verify(jwtUtils, times(1)).getJwtFromRequest(request);
        verify(userService, times(1)).getAuthenticatedUser(token);
    }

    @Test
    void testGetAuthenticatedUserProfile_NoToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(jwtUtils.getJwtFromRequest(request)).thenReturn(null);

        ResponseEntity<?> response = usersController.getAuthenticatedUserProfile(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("No token provided.", ((Map<String, String>) response.getBody()).get("error"));
        verify(jwtUtils, times(1)).getJwtFromRequest(request);
        verify(userService, never()).getAuthenticatedUser(any());
    }

    @Test
    void testGetAuthenticatedUserProfile_UserNotFound() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        String token = "validToken";

        when(jwtUtils.getJwtFromRequest(request)).thenReturn(token);
        when(userService.getAuthenticatedUser(token)).thenReturn(Optional.empty());

        ResponseEntity<?> response = usersController.getAuthenticatedUserProfile(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found.", ((Map<String, String>) response.getBody()).get("error"));
        verify(jwtUtils, times(1)).getJwtFromRequest(request);
        verify(userService, times(1)).getAuthenticatedUser(token);
    }

    @Test
    void testUpdateUser_Success() {
        SignUpRequest updateRequest = new SignUpRequest();
        String loggedInEmail = "test@example.com";

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(loggedInEmail);
        SecurityContextHolder.setContext(securityContext);

        ResponseEntity<?> response = usersController.updateUser(updateRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User updated successfully!", ((Map<String, String>) response.getBody()).get("message"));
        verify(userService, times(1)).updateUser(eq(loggedInEmail), any(SignUpRequest.class));
    }

    @Test
    void testUpdateUser_EmailAlreadyExistsException() {
        SignUpRequest updateRequest = new SignUpRequest();
        String loggedInEmail = "test@example.com";

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(loggedInEmail);
        SecurityContextHolder.setContext(securityContext);

        doThrow(new EmailAlreadyExistsException("Email already exists"))
                .when(userService).updateUser(eq(loggedInEmail), any(SignUpRequest.class));

        ResponseEntity<?> response = usersController.updateUser(updateRequest);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Email already exists", ((Map<String, String>) response.getBody()).get("error"));
        verify(userService, times(1)).updateUser(eq(loggedInEmail), any(SignUpRequest.class));
    }

    @Test
    void testDeleteUser_Success() {
        Long userId = 1L;

        ResponseEntity<?> response = usersController.deleteUser(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User deleted successfully!", ((Map<String, String>) response.getBody()).get("message"));
        verify(userService, times(1)).deleteUser(userId);
    }

    @Test
    void testDeleteUser_UserNotFoundException() {
        Long userId = 1L;

        doThrow(new UserNotFoundException("User not found"))
                .when(userService).deleteUser(userId);

        ResponseEntity<?> response = usersController.deleteUser(userId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", ((Map<String, String>) response.getBody()).get("error"));
        verify(userService, times(1)).deleteUser(userId);
    }
}
