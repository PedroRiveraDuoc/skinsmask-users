package com.duocuc.backend_srv.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.duocuc.backend_srv.dto.LoginRequest;
import com.duocuc.backend_srv.dto.SignUpRequest;
import com.duocuc.backend_srv.exception.UserAlreadyExistsException;
import com.duocuc.backend_srv.service.UserService;
import com.duocuc.backend_srv.util.JwtUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private UserService userService;

  @Autowired
  private JwtUtils jwtUtils;

  // Ruta para iniciar sesión
  @PostMapping("/login")
public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            loginRequest.getEmail(), // Cambiado a email
            loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);

    return ResponseEntity.ok(Map.of("token", jwt));
}

  // Ruta para registrar un usuario
  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
    if (userService.existsByUsername(signUpRequest.getUsername())) {
      throw new UserAlreadyExistsException("Error: Username is already taken!");
    }

    // Registrar el nuevo usuario usando el método registerUser de UserService
    userService.registerUser(
        signUpRequest.getUsername(),
        signUpRequest.getPassword(),
        signUpRequest.getEmail());

    return ResponseEntity.ok(Map.of("message", "User registered successfully!"));
  }
}