package com.duocuc.backend_srv.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.duocuc.backend_srv.dto.SignUpRequest;
import com.duocuc.backend_srv.model.Role;
import com.duocuc.backend_srv.model.User;
import com.duocuc.backend_srv.repository.RoleRepository;
import com.duocuc.backend_srv.repository.UserRepository;
import com.duocuc.backend_srv.util.JwtUtils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private UserService userService;

    // Prueba para registerUser - caso de éxito
    @Test
    public void testRegisterUser_Success() {
        // Datos de entrada
        String username = "testuser";
        String password = "password";
        String email = "test@example.com";

        // Mocks y stubs
        Role userRole = new Role();
        userRole.setCode("ROLE_USER");
        when(roleRepository.findByCode("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        // Ejecución
        User result = userService.registerUser(username, password, email);

        // Verificaciones
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
        assertEquals(email, result.getEmail());
        assertTrue(result.getRoles().contains(userRole));

        verify(roleRepository, times(1)).findByCode("ROLE_USER");
        verify(passwordEncoder, times(1)).encode(password);
        verify(userRepository, times(1)).save(any(User.class));
    }

    // Prueba para registerUser - rol no encontrado
    @Test
    public void testRegisterUser_RoleNotFound() {
        // Datos de entrada
        String username = "testuser";
        String password = "password";
        String email = "test@example.com";

        // Mocks y stubs
        when(roleRepository.findByCode("ROLE_USER")).thenReturn(Optional.empty());

        // Ejecución y verificación
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(username, password, email);
        });

        assertEquals("Error: Role not found.", exception.getMessage());
        verify(roleRepository, times(1)).findByCode("ROLE_USER");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    // Prueba para findByUsername - usuario encontrado
    @Test
    public void testFindByUsername_UserFound() {
        // Datos de entrada
        String username = "testuser";

        // Mocks y stubs
        User user = new User();
        user.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // Ejecución
        User result = userService.findByUsername(username);

        // Verificaciones
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        verify(userRepository, times(1)).findByUsername(username);
    }

    // Prueba para findByUsername - usuario no encontrado
    @Test
    public void testFindByUsername_UserNotFound() {
        // Datos de entrada
        String username = "testuser";

        // Mocks y stubs
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Ejecución y verificación
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.findByUsername(username);
        });

        assertEquals("User not found with username: " + username, exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }

    // Prueba para existsByUsername - existe
    @Test
    public void testExistsByUsername_Exists() {
        // Datos de entrada
        String username = "testuser";

        // Mocks y stubs
        when(userRepository.existsByUsername(username)).thenReturn(true);

        // Ejecución
        boolean exists = userService.existsByUsername(username);

        // Verificaciones
        assertTrue(exists);
        verify(userRepository, times(1)).existsByUsername(username);
    }

    // Prueba para existsByUsername - no existe
    @Test
    public void testExistsByUsername_NotExists() {
        // Datos de entrada
        String username = "testuser";

        // Mocks y stubs
        when(userRepository.existsByUsername(username)).thenReturn(false);

        // Ejecución
        boolean exists = userService.existsByUsername(username);

        // Verificaciones
        assertFalse(exists);
        verify(userRepository, times(1)).existsByUsername(username);
    }

    // Prueba para saveUser
    @Test
    public void testSaveUser() {
        // Datos de entrada
        User user = new User();
        user.setUsername("testuser");

        // Mocks y stubs
        when(userRepository.save(user)).thenReturn(user);

        // Ejecución
        User result = userService.saveUser(user);

        // Verificaciones
        assertNotNull(result);
        assertEquals(user, result);
        verify(userRepository, times(1)).save(user);
    }

    // Prueba para updateUser - caso de éxito
    @Test
    public void testUpdateUser_Success() {
        // Datos de entrada
        String loggedInUsername = "test@example.com";
        SignUpRequest updateRequest = new SignUpRequest();
        updateRequest.setUsername("newUsername");
        updateRequest.setEmail("new@example.com");
        updateRequest.setPassword("newPassword");

        User user = new User();
        user.setUsername("oldUsername");
        user.setEmail("test@example.com");

        // Mocks y stubs
        when(userRepository.findByEmail(loggedInUsername)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("newUsername")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        // Ejecución
        userService.updateUser(loggedInUsername, updateRequest);

        // Verificaciones
        assertEquals("newUsername", user.getUsername());
        assertEquals("new@example.com", user.getEmail());
        assertEquals("encodedNewPassword", user.getPassword());

        verify(userRepository, times(1)).findByEmail(loggedInUsername);
        verify(userRepository, times(1)).existsByUsername("newUsername");
        verify(userRepository, times(1)).existsByEmail("new@example.com");
        verify(passwordEncoder, times(1)).encode("newPassword");
        verify(userRepository, times(1)).save(user);
    }

    // Prueba para updateUser - usuario no encontrado
    @Test
    public void testUpdateUser_UserNotFound() {
        // Datos de entrada
        String loggedInUsername = "test@example.com";
        SignUpRequest updateRequest = new SignUpRequest();

        // Mocks y stubs
        when(userRepository.findByEmail(loggedInUsername)).thenReturn(Optional.empty());

        // Ejecución y verificación
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUser(loggedInUsername, updateRequest);
        });

        assertEquals("User not found.", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(loggedInUsername);
        verify(userRepository, never()).save(any(User.class));
    }

    // Prueba para updateUser - nombre de usuario ya tomado
    @Test
    public void testUpdateUser_UsernameTaken() {
        // Datos de entrada
        String loggedInUsername = "test@example.com";
        SignUpRequest updateRequest = new SignUpRequest();
        updateRequest.setUsername("takenUsername");

        User user = new User();
        user.setUsername("oldUsername");
        user.setEmail("test@example.com");

        // Mocks y stubs
        when(userRepository.findByEmail(loggedInUsername)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("takenUsername")).thenReturn(true);

        // Ejecución y verificación
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser(loggedInUsername, updateRequest);
        });

        assertEquals("Username is already taken.", exception.getMessage());
        verify(userRepository, times(1)).existsByUsername("takenUsername");
        verify(userRepository, never()).save(any(User.class));
    }

    // Prueba para deleteUser - caso de éxito
    @Test
    public void testDeleteUser_Success() {
        // Datos de entrada
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        // Mocks y stubs
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Ejecución
        userService.deleteUser(userId);

        // Verificaciones
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).delete(user);
    }

    // Prueba para deleteUser - usuario no encontrado
    @Test
    public void testDeleteUser_UserNotFound() {
        // Datos de entrada
        Long userId = 1L;

        // Mocks y stubs
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Ejecución y verificación
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.deleteUser(userId);
        });

        assertEquals("User not found.", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).delete(any(User.class));
    }

    // Prueba para getAuthenticatedUser - caso de éxito
    @Test
    public void testGetAuthenticatedUser_Success() {
        // Datos de entrada
        String token = "validToken";
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);

        // Mocks y stubs
        when(jwtUtils.getAuthenticatedUsername(token)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Ejecución
        Optional<User> result = userService.getAuthenticatedUser(token);

        // Verificaciones
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
        verify(jwtUtils, times(1)).getAuthenticatedUsername(token);
        verify(userRepository, times(1)).findByEmail(email);
    }

    // Prueba para getAuthenticatedUser - usuario no encontrado
    @Test
    public void testGetAuthenticatedUser_UserNotFound() {
        // Datos de entrada
        String token = "validToken";
        String email = "test@example.com";

        // Mocks y stubs
        when(jwtUtils.getAuthenticatedUsername(token)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Ejecución
        Optional<User> result = userService.getAuthenticatedUser(token);

        // Verificaciones
        assertFalse(result.isPresent());
        verify(jwtUtils, times(1)).getAuthenticatedUsername(token);
        verify(userRepository, times(1)).findByEmail(email);
    }
}
