package com.duocuc.backend_srv.service;

import org.springframework.beans.factory.annotation.Autowired;
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

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private JwtUtils jwtUtils;

  public User registerUser(String username, String password, String email) {
    User user = new User();
    user.setUsername(username);
    user.setPassword(passwordEncoder.encode(password));
    user.setEmail(email);

    Role userRole = roleRepository.findByCode("ROLE_USER")
        .orElseThrow(() -> new RuntimeException("Error: Role not found."));

    Set<Role> roles = new HashSet<>();
    roles.add(userRole);
    user.setRoles(roles);

    return userRepository.save(user);
  }

  public User findByUsername(String username) {
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));
  }

  public boolean existsByUsername(String username) {
    return userRepository.existsByUsername(username);
  }

  public User saveUser(User user) {
    return userRepository.save(user);
  }

  public void updateUser(String loggedInUsername, SignUpRequest updateRequest) {

    // Log de usuario autenticado
    System.out.println("Actualizando usuario autenticado: " + loggedInUsername);

    User user = userRepository.findByEmail(loggedInUsername)
        .orElseThrow(() -> new RuntimeException("User not found."));
    // Log de datos actuales del usuario antes de la actualización
    System.out.println("Datos actuales del usuario: " + user);

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
    System.out.println("Usuario actualizado correctamente: " + user);
  }

  public void deleteUser(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("User not found."));
    userRepository.delete(user);
  }

  public Optional<User> getAuthenticatedUser(String token) {
    String email = jwtUtils.getAuthenticatedUsername(token);
    return userRepository.findByEmail(email);
  }
}
