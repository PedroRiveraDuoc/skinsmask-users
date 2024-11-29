package com.duocuc.backend_srv.service;

import com.duocuc.backend_srv.dto.SignUpRequest;
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

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTests {

  @InjectMocks
  private UserService userService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private RoleRepository roleRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private JwtUtils jwtUtils;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testRegisterUserSuccess() {
    String username = "testuser";
    String password = "password123";
    String email = "testuser@example.com";

    Role mockRole = new Role();
    mockRole.setCode("ROLE_USER");

    User mockUser = new User();
    mockUser.setUsername(username);
    mockUser.setPassword("encodedPassword");
    mockUser.setEmail(email);

    when(roleRepository.findByCode("ROLE_USER")).thenReturn(Optional.of(mockRole));
    when(passwordEncoder.encode(password)).thenReturn("encodedPassword");
    when(userRepository.save(any(User.class))).thenReturn(mockUser);

    User result = userService.registerUser(username, password, email);

    assertNotNull(result);
    assertEquals(username, result.getUsername());
    assertEquals("encodedPassword", result.getPassword());
    assertEquals(email, result.getEmail());
    verify(roleRepository, times(1)).findByCode("ROLE_USER");
    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  void testRegisterUserRoleNotFound() {
    when(roleRepository.findByCode("ROLE_USER")).thenReturn(Optional.empty());

    RuntimeException exception = assertThrows(RuntimeException.class,
        () -> userService.registerUser("testuser", "password123", "testuser@example.com"));

    assertEquals("Error: Role not found.", exception.getMessage());
    verify(roleRepository, times(1)).findByCode("ROLE_USER");
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void testFindByUsernameSuccess() {
    String username = "testuser";
    User mockUser = new User();
    mockUser.setUsername(username);

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

    User result = userService.findByUsername(username);

    assertNotNull(result);
    assertEquals(username, result.getUsername());
    verify(userRepository, times(1)).findByUsername(username);
  }

  @Test
  void testFindByUsernameNotFound() {
    when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> userService.findByUsername("nonexistent"));

    assertEquals("User not found with username: nonexistent", exception.getMessage());
    verify(userRepository, times(1)).findByUsername("nonexistent");
  }

  @Test
  void testUpdateUserSuccess() {
    Long userId = 1L;
    SignUpRequest updateRequest = new SignUpRequest();
    updateRequest.setUsername("updateduser");
    updateRequest.setEmail("updated@example.com");
    updateRequest.setPassword("newpassword");

    User existingUser = new User();
    existingUser.setId(userId);
    existingUser.setUsername("olduser");
    existingUser.setEmail("old@example.com");
    existingUser.setPassword("oldpassword");

    when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
    when(passwordEncoder.encode(updateRequest.getPassword())).thenReturn("encodedNewPassword");
    when(userRepository.save(existingUser)).thenReturn(existingUser);

    userService.updateUser(userId.toString(), updateRequest);

    assertEquals("updateduser", existingUser.getUsername());
    assertEquals("updated@example.com", existingUser.getEmail());
    assertEquals("encodedNewPassword", existingUser.getPassword());
    verify(userRepository, times(1)).findById(userId);
    verify(userRepository, times(1)).save(existingUser);
  }

  @Test
  void testUpdateUserNotFound() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    RuntimeException exception = assertThrows(RuntimeException.class,
        () -> userService.updateUser("1", new SignUpRequest()));

    assertEquals("User not found", exception.getMessage());
    verify(userRepository, times(1)).findById(1L);
    verify(userRepository, never()).save(any(User.class));
  }
}
