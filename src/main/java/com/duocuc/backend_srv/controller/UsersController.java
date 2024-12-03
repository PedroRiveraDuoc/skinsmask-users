package com.duocuc.backend_srv.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.duocuc.backend_srv.dto.RoleDto;
import com.duocuc.backend_srv.dto.SignUpRequest;
import com.duocuc.backend_srv.dto.UserProfileDto;
import com.duocuc.backend_srv.model.User;
import com.duocuc.backend_srv.service.UserService;
import com.duocuc.backend_srv.util.JwtUtils;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    private static final Logger logger = LoggerFactory.getLogger(UsersController.class);

    private final UserService userService;
    private final JwtUtils jwtUtils;

    // Constructor injection
    public UsersController(UserService userService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    /**
     * Gets the authenticated user's profile.
     *
     * @param request HttpServletRequest to retrieve the JWT token.
     * @return User profile or error message.
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getAuthenticatedUserProfile(HttpServletRequest request) {
        logger.info("GET request received on /api/users/profile");

        try {
            String token = jwtUtils.getJwtFromRequest(request);
            if (token == null || token.isEmpty()) {
                logger.warn("No token provided in the request.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No token provided."));
            }

            Optional<User> userOpt = userService.getAuthenticatedUser(token);
            if (userOpt.isPresent()) {
                User user = userOpt.get();

                List<RoleDto> roles = user.getRoles().stream()
                        .map(role -> new RoleDto(role.getId(), role.getName()))
                        .collect(Collectors.toList());

                UserProfileDto userProfile = new UserProfileDto(user.getId(), user.getUsername(), roles);
                logger.info("User profile retrieved successfully for: {}", user.getUsername());
                return ResponseEntity.ok(userProfile);
            } else {
                logger.warn("User not found for the provided token.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found."));
            }
        } catch (Exception e) {
            logger.error("Error retrieving user profile: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error retrieving user profile."));
        }
    }

    /**
     * Updates the authenticated user's profile.
     *
     * @param updateRequest User data for the update.
     * @return Success or error message.
     */
    @PutMapping("/update")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateUser(@Valid @RequestBody SignUpRequest updateRequest) {
        logger.info("PUT request received on /api/users/update");

        try {
            String loggedInUsername = SecurityContextHolder.getContext().getAuthentication().getName();
            logger.info("Authenticated user: {}", loggedInUsername);

            userService.updateUser(loggedInUsername, updateRequest);
            logger.info("User updated successfully: {}", loggedInUsername);
            return ResponseEntity.ok(Map.of("message", "User updated successfully!"));
        } catch (IllegalArgumentException e) {
            logger.warn("Validation error during user update: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            logger.error("Unexpected error during user update: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unexpected error: " + e.getMessage()));
        }
    }

    /**
     * Deletes a user (only accessible to administrators).
     *
     * @param id ID of the user to delete.
     * @return Success or error message.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        logger.info("DELETE request received on /api/users/{}", id);

        try {
            userService.deleteUser(id);
            logger.info("User deleted successfully. ID: {}", id);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully!"));
        } catch (Exception e) {
            logger.error("Error deleting user. ID: {}, Error: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error deleting user."));
        }
    }
}
