package com.duocuc.backend_srv.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.duocuc.backend_srv.config.JwtConfig;

import jakarta.servlet.http.HttpServletRequest;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtils {

    @Autowired
    private JwtConfig jwtConfig;

    // Obtener la clave secreta en formato Key
    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtConfig.getSecret());
        return Keys.hmacShaKeyFor(keyBytes); // Usa una clave de al menos 512 bits
    }

    // Generar el token JWT
    public String generateJwtToken(Authentication authentication) {
        String email = authentication.getName(); // Ahora es el email
        String roles = authentication.getAuthorities().stream()
            .map(grantedAuthority -> grantedAuthority.getAuthority())
            .reduce((a, b) -> a + "," + b).orElse("");

        System.out.println("Generando token JWT para email: " + email);
        System.out.println("Roles asignados: " + roles);

        return Jwts.builder()
            .setSubject(email) // Usar email como sujeto
            .claim("roles", roles) // Agregar roles al token
            .setIssuedAt(new Date())
            .setExpiration(new Date((new Date()).getTime() + jwtConfig.getExpirationMs()))
            .signWith(getSigningKey(), SignatureAlgorithm.HS512)
            .compact();
    }

    // Validar el token JWT
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            System.out.println("Token válido: " + token);
            return true;
        } catch (ExpiredJwtException e) {
            System.err.println("El token ha expirado: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.err.println("Token no soportado: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.err.println("Token malformado: " + e.getMessage());
        } catch (SignatureException e) {
            System.err.println("Fallo en la firma del token: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Token vacío o nulo: " + e.getMessage());
        }
        return false;
    }

    // Obtener Claims desde el token JWT
    public Claims getClaimsFromToken(String token) {
        System.out.println("Extrayendo claims del token...");
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    // Obtener el token JWT desde la solicitud
    public String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7); // Quita "Bearer " para obtener solo el token
            System.out.println("Token extraído de la solicitud: " + token);
            return token;
        }
        System.err.println("Token no encontrado en la cabecera de la solicitud.");
        return null;
    }

    // Obtener el usuario autenticado desde el token
    public String getAuthenticatedUsername(String token) {
        Claims claims = getClaimsFromToken(token);
        String username = claims.getSubject(); // Obtener el usuario desde las claims
        System.out.println("Usuario autenticado extraído del token: " + username);
        return username;
    }
}
