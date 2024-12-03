package com.duocuc.backend_srv.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.duocuc.backend_srv.dto.SignUpRequest;
import com.duocuc.backend_srv.model.Role;
import com.duocuc.backend_srv.model.User;
import com.duocuc.backend_srv.repository.RoleRepository;
import com.duocuc.backend_srv.repository.UserRepository;
import com.duocuc.backend_srv.util.JwtUtils;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtils jwtUtils;

  // Constructor injection
  public UserService(UserRepository userRepository, RoleRepository roleRepository,
      PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtils = jwtUtils;
  }

  /**
   * Registers a new user with a default role.
   *
   * @param username The username of the new user.
   * @param password The password of the new user.
   * @param email    The email of the new user.
   * @return The saved User object.
   */
  public User registerUser(String username, String password, String email) {
    User user = new User();
    user.setUsername(username);
    user.setEmail(email);

    Role userRole = roleRepository.findByCode("ROLE_USER")
        .orElseThrow(() -> new RuntimeException("Error: Role not found."));

    user.setPassword(passwordEncoder.encode(password)); // Mover esta línea después de obtener el rol

    Set<Role> roles = new HashSet<>();
    roles.add(userRole);
    user.setRoles(roles);

    return userRepository.save(user);
}

  /**
   * Finds a user by their username.
   *
   * @param username The username to search for.
   * @return The found User object.
   * @throws IllegalArgumentException if the user is not found.
   */
  public User findByUsername(String username) {
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));
  }

  /**
   * Checks if a username already exists in the database.
   *
   * @param username The username to check.
   * @return True if the username exists, otherwise false.
   */
  public boolean existsByUsername(String username) {
    return userRepository.existsByUsername(username);
  }

  /**
   * Saves or updates a User object in the database.
   *
   * @param user The user to save.
   * @return The saved User object.
   */
  public User saveUser(User user) {
    return userRepository.save(user);
  }

  /**
   * Updates an authenticated user's profile.
   *
   * @param loggedInUsername The username of the authenticated user.
   * @param updateRequest    The user details to update.
   * @throws IllegalArgumentException if validation fails.
   */
  public void updateUser(String loggedInUsername, SignUpRequest updateRequest) {
    User user = userRepository.findByEmail(loggedInUsername)
        .orElseThrow(() -> new RuntimeException("User not found."));

    if (updateRequest.getUsername() != null &&
        !updateRequest.getUsername().equals(user.getUsername()) &&
        userRepository.existsByUsername(updateRequest.getUsername())) {
      throw new IllegalArgumentException("Username is already taken.");
    }

    if (updateRequest.getEmail() != null &&
        !updateRequest.getEmail().equals(user.getEmail()) &&
        userRepository.existsByEmail(updateRequest.getEmail())) {
      throw new IllegalArgumentException("Email is already registered.");
    }

    if (updateRequest.getUsername() != null) {
      user.setUsername(updateRequest.getUsername());
    }

    if (updateRequest.getEmail() != null) {
      user.setEmail(updateRequest.getEmail());
    }

    if (updateRequest.getPassword() != null && !updateRequest.getPassword().isEmpty()) {
      user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
    }

    userRepository.save(user);
  }

  /**
   * Deletes a user by their ID.
   *
   * @param id The ID of the user to delete.
   */
  public void deleteUser(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("User not found."));
    userRepository.delete(user);
  }

  /**
   * Retrieves the authenticated user based on the provided token.
   *
   * @param token The authentication token.
   * @return An Optional containing the User object.
   */
  public Optional<User> getAuthenticatedUser(String token) {
    String email = jwtUtils.getAuthenticatedUsername(token);
    return userRepository.findByEmail(email);
  }
}
