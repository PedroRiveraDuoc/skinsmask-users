package com.duocuc.backend_srv.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.duocuc.backend_srv.dto.SignUpRequest;
import com.duocuc.backend_srv.exception.EmailAlreadyExistsException;
import com.duocuc.backend_srv.exception.UserNotFoundException;
import com.duocuc.backend_srv.exception.UsernameAlreadyExistsException;
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
   * @param username  The username of the new user.
   * @param password  The password of the new user.
   * @param email     The email of the new user.
   * @param firstName The first name of the new user.
   * @param lastName  The last name of the new user.
   * @return The saved User object.
   */
  public User registerUser(SignUpRequest signUpRequest) {
    // Crear un nuevo usuario
    User user = new User(
        signUpRequest.getUsername(),
        passwordEncoder.encode(signUpRequest.getPassword()),
        signUpRequest.getEmail(),
        signUpRequest.getFirstName(),
        signUpRequest.getLastName());

    // Obtener roles enviados en la solicitud
    Set<String> strRoles = signUpRequest.getRoles();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null || strRoles.isEmpty()) {
      // Asignar el rol predeterminado (ROLE_USER) si no se especificaron roles
      Role userRole = roleRepository.findByCode("ROLE_USER")
          .orElseThrow(() -> new RuntimeException("Error: Role USER not found."));
      roles.add(userRole);
    } else {
      // Verificar y asignar los roles enviados
      for (String role : strRoles) {
        Role foundRole = roleRepository.findByCode(role)
            .orElseThrow(() -> new RuntimeException("Error: Role " + role + " not found."));
        roles.add(foundRole);
      }
    }

    user.setRoles(roles); // Asignar los roles al usuario
    return userRepository.save(user); // Guardar el usuario en la base de datos
  }

  /**
   * Finds a user by their username.
   *
   * @param username The username to search for.
   * @return The found User object.
   * @throws UsernameAlreadyExistsException if the user is not found.
   */
  public User findByUsername(String username) {
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameAlreadyExistsException("User not found with username: " + username));
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
   * @param loggedInEmail The email of the authenticated user.
   * @param updateRequest The user details to update.
   * @throws UsernameAlreadyExistsException if the username is already taken.
   * @throws EmailAlreadyExistsException    if the email is already registered.
   * @throws UserNotFoundException          if the user is not found.
   */
  public void updateUser(String loggedInEmail, SignUpRequest updateRequest) {
    User user = userRepository.findByEmail(loggedInEmail)
        .orElseThrow(() -> new UserNotFoundException("User not found."));

    if (updateRequest.getUsername() != null &&
        !updateRequest.getUsername().equals(user.getUsername()) &&
        userRepository.existsByUsername(updateRequest.getUsername())) {
      throw new UsernameAlreadyExistsException("Username is already taken.");
    }

    if (updateRequest.getEmail() != null &&
        !updateRequest.getEmail().equals(user.getEmail()) &&
        userRepository.existsByEmail(updateRequest.getEmail())) {
      throw new EmailAlreadyExistsException("Email is already registered.");
    }

    if (updateRequest.getUsername() != null) {
      user.setUsername(updateRequest.getUsername());
    }

    if (updateRequest.getEmail() != null) {
      user.setEmail(updateRequest.getEmail());
    }

    if (updateRequest.getFirstName() != null) {
      user.setFirstName(updateRequest.getFirstName());
    }

    if (updateRequest.getLastName() != null) {
      user.setLastName(updateRequest.getLastName());
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
   * @throws UserNotFoundException if the user is not found.
   */
  public void deleteUser(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException("User not found."));
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

  public boolean existsByEmail(String email) {
    return userRepository.existsByEmail(email);
  }
}
