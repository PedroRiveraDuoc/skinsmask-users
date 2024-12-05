package com.duocuc.backend_srv.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.Map;

import com.duocuc.backend_srv.dto.LoginRequest;
import com.duocuc.backend_srv.dto.SignUpRequest;
import com.duocuc.backend_srv.model.User;
import com.duocuc.backend_srv.service.UserService;
import com.duocuc.backend_srv.util.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserService userService;

    @Mock
    private JwtUtils jwtUtils;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void testRegisterUser_Success() {
        // Mock de SignUpRequest
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setUsername("testuser");
        signUpRequest.setPassword("Test@1234");
        signUpRequest.setEmail("testuser@example.com");

        // Mock del User que será devuelto por el servicio
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");

        // Configuración de los mocks
        Mockito.when(userService.existsByUsername("testuser")).thenReturn(false);
        Mockito.when(userService.existsByEmail("testuser@example.com")).thenReturn(false);
        Mockito.when(userService.registerUser(any(String.class), any(String.class), any(String.class)))
                .thenReturn(user);

        // Llamada al método del controlador
        ResponseEntity<?> response = authController.registerUser(signUpRequest);

        // Validación de la respuesta
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody(), "Response body is null");
        assertEquals(1L, ((Map<?, ?>) response.getBody()).get("id"));
        assertEquals("testuser", ((Map<?, ?>) response.getBody()).get("username"));
        assertEquals("testuser@example.com", ((Map<?, ?>) response.getBody()).get("email"));
        assertEquals("User registered successfully!", ((Map<?, ?>) response.getBody()).get("message"));

        // Verificación de interacciones con los mocks
        Mockito.verify(userService).existsByUsername("testuser");
        Mockito.verify(userService).existsByEmail("testuser@example.com");
        Mockito.verify(userService).registerUser("testuser", "Test@1234", "testuser@example.com");
    }

}