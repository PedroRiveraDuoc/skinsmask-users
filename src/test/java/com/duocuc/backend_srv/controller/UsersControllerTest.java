package com.duocuc.backend_srv.controller;

import com.duocuc.backend_srv.dto.RoleDto;
import com.duocuc.backend_srv.dto.SignUpRequest;
import com.duocuc.backend_srv.dto.UserProfileDto;
import com.duocuc.backend_srv.model.Role;
import com.duocuc.backend_srv.model.User;
import com.duocuc.backend_srv.service.UserService;
import com.duocuc.backend_srv.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsersControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private UsersController usersController;

    @BeforeEach
    public void setUp() {
        // No es necesario configurar nada adicional aquí
    }

    // Prueba para getAuthenticatedUserProfile - caso exitoso
    @Test
    public void testGetAuthenticatedUserProfile_Success() {
        // Datos de entrada
        String token = "validToken";
        String username = "testuser";
        Long userId = 1L;
        String roleName = "USER";
        Long roleId = 1L;

        // Configuración de mocks
        when(jwtUtils.getJwtFromRequest(request)).thenReturn(token);

        User user = new User();
        user.setId(userId);
        user.setUsername(username);

        Role role = new Role();
        role.setId(roleId);
        role.setName(roleName);
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        when(userService.getAuthenticatedUser(token)).thenReturn(Optional.of(user));

        // Ejecución
        ResponseEntity<?> response = usersController.getAuthenticatedUserProfile(request);

        // Verificaciones
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof UserProfileDto);

        UserProfileDto userProfile = (UserProfileDto) response.getBody();
        assertNotNull(userProfile);
        assertEquals(userId, userProfile.getId());
        assertEquals(username, userProfile.getUsername());
        assertEquals(1, userProfile.getRoles().size());
        assertEquals(roleName, userProfile.getRoles().get(0).getName());

        verify(jwtUtils, times(1)).getJwtFromRequest(request);
        verify(userService, times(1)).getAuthenticatedUser(token);
    }

    // Prueba para getAuthenticatedUserProfile - sin token proporcionado
    @Test
    public void testGetAuthenticatedUserProfile_NoToken() {
        // Configuración de mocks
        when(jwtUtils.getJwtFromRequest(request)).thenReturn(null);

        // Ejecución
        ResponseEntity<?> response = usersController.getAuthenticatedUserProfile(request);

        // Verificaciones
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);

        Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("No token provided.", responseBody.get("error"));

        verify(jwtUtils, times(1)).getJwtFromRequest(request);
        verify(userService, never()).getAuthenticatedUser(anyString());
    }

    // Prueba para getAuthenticatedUserProfile - usuario no encontrado
    @Test
    public void testGetAuthenticatedUserProfile_UserNotFound() {
        // Datos de entrada
        String token = "validToken";

        // Configuración de mocks
        when(jwtUtils.getJwtFromRequest(request)).thenReturn(token);
        when(userService.getAuthenticatedUser(token)).thenReturn(Optional.empty());

        // Ejecución
        ResponseEntity<?> response = usersController.getAuthenticatedUserProfile(request);

        // Verificaciones
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);

        Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("User not found.", responseBody.get("error"));

        verify(jwtUtils, times(1)).getJwtFromRequest(request);
        verify(userService, times(1)).getAuthenticatedUser(token);
    }

    // Prueba para getAuthenticatedUserProfile - excepción durante la recuperación
    @Test
    public void testGetAuthenticatedUserProfile_Exception() {
        // Datos de entrada
        String token = "validToken";

        // Configuración de mocks
        when(jwtUtils.getJwtFromRequest(request)).thenReturn(token);
        when(userService.getAuthenticatedUser(token)).thenThrow(new RuntimeException("Database error"));

        // Ejecución
        ResponseEntity<?> response = usersController.getAuthenticatedUserProfile(request);

        // Verificaciones
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);

        Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Error retrieving user profile.", responseBody.get("error"));

        verify(jwtUtils, times(1)).getJwtFromRequest(request);
        verify(userService, times(1)).getAuthenticatedUser(token);
    }

    // Prueba para updateUser - caso exitoso
    @Test
    public void testUpdateUser_Success() {
        // Datos de entrada
        SignUpRequest updateRequest = new SignUpRequest();
        updateRequest.setUsername("newUsername");
        updateRequest.setEmail("new@example.com");
        updateRequest.setPassword("newPassword");

        String loggedInUsername = "currentUsername";

        // Configuración del contexto de seguridad
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(loggedInUsername);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        // Ejecución
        ResponseEntity<?> response = usersController.updateUser(updateRequest);

        // Verificaciones
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);

        Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("User updated successfully!", responseBody.get("message"));

        verify(userService, times(1)).updateUser(loggedInUsername, updateRequest);

        // Limpieza del contexto de seguridad
        SecurityContextHolder.clearContext();
    }

    // Prueba para updateUser - error de validación
    @Test
    public void testUpdateUser_ValidationError() {
        // Datos de entrada
        SignUpRequest updateRequest = new SignUpRequest();
        updateRequest.setUsername("takenUsername");

        String loggedInUsername = "currentUsername";

        // Configuración del contexto de seguridad
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(loggedInUsername);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        // Configuración de mocks
        doThrow(new IllegalArgumentException("Username is already taken."))
                .when(userService).updateUser(loggedInUsername, updateRequest);

        // Ejecución
        ResponseEntity<?> response = usersController.updateUser(updateRequest);

        // Verificaciones
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);

        Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Username is already taken.", responseBody.get("error"));

        verify(userService, times(1)).updateUser(loggedInUsername, updateRequest);

        // Limpieza del contexto de seguridad
        SecurityContextHolder.clearContext();
    }

    // Prueba para updateUser - excepción inesperada
    @Test
    public void testUpdateUser_Exception() {
        // Datos de entrada
        SignUpRequest updateRequest = new SignUpRequest();

        String loggedInUsername = "currentUsername";

        // Configuración del contexto de seguridad
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(loggedInUsername);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        // Configuración de mocks
        doThrow(new RuntimeException("Database error"))
                .when(userService).updateUser(loggedInUsername, updateRequest);

        // Ejecución
        ResponseEntity<?> response = usersController.updateUser(updateRequest);

        // Verificaciones
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);

        Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Unexpected error: Database error", responseBody.get("error"));

        verify(userService, times(1)).updateUser(loggedInUsername, updateRequest);

        // Limpieza del contexto de seguridad
        SecurityContextHolder.clearContext();
    }


}
