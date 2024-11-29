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

  // Método para obtener la clave secreta en formato Key
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
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  // Obtener Claims desde el token JWT
  public Claims getClaimsFromToken(String token) {
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
      return bearerToken.substring(7); // Quita "Bearer " para obtener solo el token
    }
    return null;
  }

  public String getAuthenticatedUsername(String token) {
    Claims claims = getClaimsFromToken(token);
    return claims.getSubject(); // Obtener el usuario (generalmente el nombre de usuario) desde las claims
  }
}
