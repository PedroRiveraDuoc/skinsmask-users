package com.duocuc.backend_srv.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.duocuc.backend_srv.config.JwtConfig;
import com.duocuc.backend_srv.security.UserPrincipal;

import jakarta.servlet.http.HttpServletRequest;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    private final JwtConfig jwtConfig;

    // Constructor injection for JwtConfig
    public JwtUtils(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    /**
     * Generates the signing key from the JWT secret.
     *
     * @return The signing key.
     */
    protected Key getSigningKey() {
        String secret = jwtConfig.getSecret();
        if (secret == null || secret.isEmpty()) {
            throw new IllegalStateException("JWT secret cannot be null or empty");
        }
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        if (keyBytes.length < 32) { // 32 bytes = 256 bits
            throw new IllegalArgumentException("JWT secret key must be at least 256 bits");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generates a JWT token for a given authentication.
     *
     * @param authentication The authentication object containing user details.
     * @return The generated JWT token.
     */
    public String generateJwtToken(Authentication authentication) {
    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

    return Jwts.builder()
        .setSubject(userPrincipal.getEmail()) // Email como subject
        .claim("id", userPrincipal.getId()) // Agregar ID
        .claim("username", userPrincipal.getUsername()) // Agregar username
        .claim("roles", userPrincipal.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(",")))
        .setIssuedAt(new Date())
        .setExpiration(new Date((new Date()).getTime() + jwtConfig.getExpirationMs()))
        .signWith(getSigningKey(), SignatureAlgorithm.HS512)
        .compact();
}

    /**
     * Validates the JWT token.
     *
     * @param token The JWT token to validate.
     * @return True if the token is valid, false otherwise.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            logger.error("Token expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported token: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Malformed token: {}", e.getMessage());
        } catch (SignatureException e) {
            logger.error("Invalid signature: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Empty or null token: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Extracts claims from a JWT token.
     *
     * @param token The JWT token.
     * @return The claims extracted from the token.
     */
    public Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        } catch (JwtException e) {
            logger.error("Failed to extract claims from token: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Retrieves the JWT token from the HTTP request header.
     *
     * @param request The HTTP request.
     * @return The JWT token, or null if not found.
     */
    public String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Remove "Bearer " prefix
        }
        logger.warn("JWT token not found in request header");
        return null;
    }

    /**
     * Extracts the authenticated username from the JWT token.
     *
     * @param token The JWT token.
     * @return The username extracted from the token.
     */
    public String getAuthenticatedUsername(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject(); // Retrieves the username from the claims
    }

}
