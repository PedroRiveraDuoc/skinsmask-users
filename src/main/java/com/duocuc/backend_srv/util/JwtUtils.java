package com.duocuc.backend_srv.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.duocuc.backend_srv.config.JwtConfig;

import jakarta.servlet.http.HttpServletRequest;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtils {

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
    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtConfig.getSecret());
        return Keys.hmacShaKeyFor(keyBytes); // Uses a key of at least 512 bits
    }

    /**
     * Generates a JWT token for a given authentication.
     *
     * @param authentication The authentication object containing user details.
     * @return The generated JWT token.
     */
    public String generateJwtToken(Authentication authentication) {
        String email = authentication.getName(); // Now using email
        String roles = authentication.getAuthorities().stream()
            .map(grantedAuthority -> grantedAuthority.getAuthority())
            .reduce((a, b) -> a + "," + b).orElse("");

        return Jwts.builder()
            .setSubject(email) // Use email as the subject
            .claim("roles", roles) // Add roles as a claim
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
            System.err.println("Token expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.err.println("Unsupported token: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.err.println("Malformed token: " + e.getMessage());
        } catch (SignatureException e) {
            System.err.println("Invalid signature: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Empty or null token: " + e.getMessage());
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
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
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
