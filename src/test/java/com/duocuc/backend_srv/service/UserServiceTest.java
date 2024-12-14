package com.duocuc.backend_srv.service;

import com.duocuc.backend_srv.dto.SignUpRequest;
import com.duocuc.backend_srv.exception.EmailAlreadyExistsException;
import com.duocuc.backend_srv.exception.UserNotFoundException;
import com.duocuc.backend_srv.exception.UsernameAlreadyExistsException;
import com.duocuc.backend_srv.model.Role;
import com.duocuc.backend_srv.model.User;
import com.duocuc.backend_srv.repository.RoleRepository;
import com.duocuc.backend_srv.repository.UserRepository;
import com.duocuc.backend_srv.util.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUserWithDefaultRole() {
        // Arrange
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setUsername("testuser");
        signUpRequest.setPassword("password123");
        signUpRequest.setEmail("test@example.com");
        signUpRequest.setFirstName("John");
        signUpRequest.setLastName("Doe");

        Role defaultRole = new Role();
        defaultRole.setCode("ROLE_USER");

        User user = new User("testuser", "encodedPassword", "test@example.com", "John", "Doe");
        user.setRoles(Set.of(defaultRole));

        when(roleRepository.findByCode("ROLE_USER")).thenReturn(Optional.of(defaultRole));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User savedUser = userService.registerUser(signUpRequest);

        // Assert
        assertEquals("testuser", savedUser.getUsername());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals("test@example.com", savedUser.getEmail());
        assertTrue(savedUser.getRoles().contains(defaultRole));

        verify(roleRepository, times(1)).findByCode("ROLE_USER");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testFindByUsername_UserExists() {
        // Arrange
        User user = new User();
        user.setUsername("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Act
        User foundUser = userService.findByUsername("testuser");

        // Assert
        assertNotNull(foundUser);
        assertEquals("testuser", foundUser.getUsername());
    }

    @Test
    void testFindByUsername_UserDoesNotExist() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameAlreadyExistsException.class, () -> userService.findByUsername("testuser"));
    }

    @Test
    void testExistsByUsername() {
        // Arrange
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // Act
        boolean exists = userService.existsByUsername("testuser");

        // Assert
        assertTrue(exists);
    }

    @Test
    void testSaveUser() {
        // Arrange
        User user = new User();
        when(userRepository.save(user)).thenReturn(user);

        // Act
        User savedUser = userService.saveUser(user);

        // Assert
        assertNotNull(savedUser);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdateUser_ValidRequest() {
        // Arrange
        User user = new User("testuser", "encodedPassword", "test@example.com", "John", "Doe");
        SignUpRequest updateRequest = new SignUpRequest();
        updateRequest.setUsername("newUsername");
        updateRequest.setEmail("newEmail@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("newUsername")).thenReturn(false);
        when(userRepository.existsByEmail("newEmail@example.com")).thenReturn(false);

        // Act
        userService.updateUser("test@example.com", updateRequest);

        // Assert
        assertEquals("newUsername", user.getUsername());
        assertEquals("newEmail@example.com", user.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testDeleteUser_UserExists() {
        // Arrange
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void testDeleteUser_UserNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(1L));
    }

    @Test
    void testGetAuthenticatedUser() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");

        when(jwtUtils.getAuthenticatedUsername("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // Act
        Optional<User> authenticatedUser = userService.getAuthenticatedUser("token");

        // Assert
        assertTrue(authenticatedUser.isPresent());
        assertEquals("test@example.com", authenticatedUser.get().getEmail());
    }

    @Test
    void testExistsByEmail() {
        // Arrange
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // Act
        boolean exists = userService.existsByEmail("test@example.com");

        // Assert
        assertTrue(exists);
    }

    @Test
    void testRegisterUserWithSpecificRoles() {
        // Arrange
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setUsername("testuser");
        signUpRequest.setPassword("password123");
        signUpRequest.setEmail("test@example.com");
        signUpRequest.setFirstName("John");
        signUpRequest.setLastName("Doe");
        signUpRequest.setRoles(Set.of("ROLE_ADMIN", "ROLE_MANAGER"));

        Role adminRole = new Role();
        adminRole.setCode("ROLE_ADMIN");

        Role managerRole = new Role();
        managerRole.setCode("ROLE_MANAGER");

        when(roleRepository.findByCode("ROLE_ADMIN")).thenReturn(Optional.of(adminRole));
        when(roleRepository.findByCode("ROLE_MANAGER")).thenReturn(Optional.of(managerRole));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User savedUser = userService.registerUser(signUpRequest);

        // Assert
        assertEquals("testuser", savedUser.getUsername());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals("test@example.com", savedUser.getEmail());
        assertTrue(savedUser.getRoles().contains(adminRole));
        assertTrue(savedUser.getRoles().contains(managerRole));

        verify(roleRepository, times(1)).findByCode("ROLE_ADMIN");
        verify(roleRepository, times(1)).findByCode("ROLE_MANAGER");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUserWithInvalidRole() {
        // Arrange
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setUsername("testuser");
        signUpRequest.setPassword("password123");
        signUpRequest.setEmail("test@example.com");
        signUpRequest.setFirstName("John");
        signUpRequest.setLastName("Doe");
        signUpRequest.setRoles(Set.of("ROLE_INVALID"));

        when(roleRepository.findByCode("ROLE_INVALID")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.registerUser(signUpRequest));
        assertEquals("Error: Role ROLE_INVALID not found.", exception.getMessage());
        verify(roleRepository, times(1)).findByCode("ROLE_INVALID");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateUserWithExistingUsername() {
        // Arrange
        User user = new User("testuser", "encodedPassword", "test@example.com", "John", "Doe");
        SignUpRequest updateRequest = new SignUpRequest();
        updateRequest.setUsername("existingUsername");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("existingUsername")).thenReturn(true);

        // Act & Assert
        UsernameAlreadyExistsException exception = assertThrows(UsernameAlreadyExistsException.class,
                () -> userService.updateUser("test@example.com", updateRequest));
        assertEquals("Username is already taken.", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(userRepository, times(1)).existsByUsername("existingUsername");
    }

    @Test
    void testUpdateUserWithExistingEmail() {
        // Arrange
        User user = new User("testuser", "encodedPassword", "test@example.com", "John", "Doe");
        SignUpRequest updateRequest = new SignUpRequest();
        updateRequest.setEmail("existing@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // Act & Assert
        EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class,
                () -> userService.updateUser("test@example.com", updateRequest));
        assertEquals("Email is already registered.", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(userRepository, times(1)).existsByEmail("existing@example.com");
    }

    @Test
    void testUpdateUserWithValidRequest() {
        // Arrange
        User user = new User("testuser", "encodedPassword", "test@example.com", "John", "Doe");
        SignUpRequest updateRequest = new SignUpRequest();
        updateRequest.setUsername("newUsername");
        updateRequest.setEmail("newEmail@example.com");
        updateRequest.setFirstName("NewFirstName");
        updateRequest.setLastName("NewLastName");
        updateRequest.setPassword("newPassword");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        // Act
        userService.updateUser("test@example.com", updateRequest);

        // Assert
        assertEquals("newUsername", user.getUsername());
        assertEquals("newEmail@example.com", user.getEmail());
        assertEquals("NewFirstName", user.getFirstName());
        assertEquals("NewLastName", user.getLastName());
        assertEquals("encodedNewPassword", user.getPassword());
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(userRepository, times(1)).save(user);
    }

}
