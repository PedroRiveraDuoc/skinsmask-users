package com.duocuc.backend_srv.controller;

import java.util.Map;

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

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    // Constructor injection
    public AuthController(AuthenticationManager authenticationManager, UserService userService, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    /**
     * Endpoint to authenticate user and generate a JWT token.
     *
     * @param loginRequest the login request containing email and password.
     * @return ResponseEntity with the generated JWT token.
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        return ResponseEntity.ok(Map.of("token", jwt));
    }

    /**
     * Endpoint to register a new user.
     *
     * @param signUpRequest the signup request containing user details.
     * @return ResponseEntity with a success message.
     * @throws UserAlreadyExistsException if the username is already taken.
     */
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        if (userService.existsByUsername(signUpRequest.getUsername())) {
            throw new UserAlreadyExistsException("Error: Username is already taken!");
        }

        userService.registerUser(
            signUpRequest.getUsername(),
            signUpRequest.getPassword(),
            signUpRequest.getEmail()
        );

        return ResponseEntity.ok(Map.of("message", "User registered successfully!"));
    }
}
