package com.duocuc.backend_srv.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * Obtiene el perfil del usuario autenticado.
     *
     * @param request HttpServletRequest para obtener el token JWT.
     * @return Perfil del usuario o mensaje de error.
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getAuthenticatedUserProfile(HttpServletRequest request) {
        System.out.println("Método HTTP recibido: " + request.getMethod());
        logger.info("Solicitud GET recibida en /api/users/profile");
        try {
            // Extraer el token del encabezado de la solicitud
            String token = jwtUtils.getJwtFromRequest(request);
            System.out.println("Token recibido: " + token);

            if (token == null || token.isEmpty()) {
                logger.warn("Token no proporcionado en la solicitud.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No token provided."));
            }

            // Obtener el usuario autenticado basado en el token
            Optional<User> userOpt = userService.getAuthenticatedUser(token);

            System.out.println("Usuario autenticado: " + (userOpt.isPresent() ? userOpt.get().getEmail() : "No encontrado"));

            if (userOpt.isPresent()) {
                User user = userOpt.get();

                // Mapear los roles del usuario
                List<RoleDto> roles = user.getRoles().stream()
                        .map(role -> new RoleDto(role.getId(), role.getName()))
                        .collect(Collectors.toList());

                // Construir el DTO del perfil del usuario
                UserProfileDto userProfile = new UserProfileDto(user.getId(), user.getUsername(), roles);

                logger.info("Perfil del usuario obtenido correctamente para: {}", user.getUsername());
                return ResponseEntity.ok(userProfile);
            } else {
                logger.warn("Usuario no encontrado para el token proporcionado.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found."));
            }
        } catch (Exception e) {
            logger.error("Error al obtener el perfil del usuario: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error retrieving user profile."));
        }
    }

    /**
     * Actualiza el perfil del usuario autenticado.
     *
     * @param updateRequest Datos del usuario para actualizar.
     * @return Mensaje de éxito o error.
     */
    @PutMapping("/update")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateUser(@Valid @RequestBody SignUpRequest updateRequest) {
        logger.info("Solicitud PUT recibida en /api/users/update");
        try {
            // Obtener el usuario autenticado
            String loggedInUsername = SecurityContextHolder.getContext().getAuthentication().getName();

            // Logs para depuración
            logger.info("Usuario autenticado: {}", loggedInUsername);
            logger.info("Datos recibidos para actualización: {}", updateRequest);

            // Llamar al servicio para actualizar el usuario
            userService.updateUser(loggedInUsername, updateRequest);

            logger.info("Usuario actualizado correctamente: {}", loggedInUsername);
            return ResponseEntity.ok(Map.of("message", "User updated successfully!"));
        } catch (IllegalArgumentException e) {
            logger.warn("Error de validación al actualizar usuario: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            logger.error("Error inesperado al actualizar usuario: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unexpected error: " + e.getMessage()));
        }
    }

    /**
     * Elimina un usuario (solo permitido para administradores).
     *
     * @param id ID del usuario a eliminar.
     * @return Mensaje de éxito o error.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            logger.info("Usuario eliminado correctamente. ID: {}", id);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully!"));
        } catch (Exception e) {
            logger.error("Error al eliminar usuario. ID: {}, Error: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error deleting user."));
        }
    }
}
