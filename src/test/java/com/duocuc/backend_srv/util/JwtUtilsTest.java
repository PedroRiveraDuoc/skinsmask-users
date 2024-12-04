package com.duocuc.backend_srv.util;

import com.duocuc.backend_srv.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import jakarta.servlet.http.HttpServletRequest;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @Mock
    private JwtConfig jwtConfig;

    @Mock
    private Authentication authentication;

    @Mock
    private HttpServletRequest request;

    private Key strongKey;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Configurar clave segura para pruebas
        strongKey = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS512);
        String encodedKey = Base64.getEncoder().encodeToString(strongKey.getEncoded());

        when(jwtConfig.getSecret()).thenReturn(encodedKey);
        when(jwtConfig.getExpirationMs()).thenReturn(3600000); // 1 hora en milisegundos

        jwtUtils = new JwtUtils(jwtConfig);
    }


    @Test
    public void testValidateToken_Valid() {
        String token = Jwts.builder()
                .setSubject("test@example.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hora
                .signWith(strongKey)
                .compact();

        assertTrue(jwtUtils.validateToken(token), "El token debe ser válido.");
    }

    @Test
    public void testValidateToken_InvalidSignature() {
        String token = Jwts.builder()
                .setSubject("test@example.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hora
                .signWith(Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256))
                .compact();

        assertFalse(jwtUtils.validateToken(token), "El token debe ser inválido debido a la firma incorrecta.");
    }

    @Test
    public void testValidateToken_Expired() {
        String token = Jwts.builder()
                .setSubject("test@example.com")
                .setIssuedAt(new Date(System.currentTimeMillis() - 3600000)) // 1 hora antes
                .setExpiration(new Date(System.currentTimeMillis() - 1800000)) // Expirado hace 30 minutos
                .signWith(strongKey)
                .compact();

        assertFalse(jwtUtils.validateToken(token), "El token debe ser inválido porque está expirado.");
    }

    @Test
    public void testGetClaimsFromToken() {
        String token = Jwts.builder()
                .setSubject("test@example.com")
                .claim("roles", "ROLE_USER")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hora
                .signWith(strongKey)
                .compact();

        Claims claims = jwtUtils.getClaimsFromToken(token);
        assertEquals("test@example.com", claims.getSubject(), "El subject del token debe coincidir con el email.");
        assertEquals("ROLE_USER", claims.get("roles"), "El token debe contener los roles correctos.");
    }

    @Test
    public void testGetJwtFromRequest_ValidHeader() {
        when(request.getHeader("Authorization")).thenReturn("Bearer my-jwt-token");

        String token = jwtUtils.getJwtFromRequest(request);
        assertEquals("my-jwt-token", token, "El token extraído debe coincidir con el header.");
    }

    @Test
    public void testGetJwtFromRequest_InvalidHeader() {
        when(request.getHeader("Authorization")).thenReturn("InvalidToken");

        String token = jwtUtils.getJwtFromRequest(request);
        assertNull(token, "El token debe ser nulo si el header no es válido.");
    }

    @Test
    public void testGetAuthenticatedUsername() {
        String token = Jwts.builder()
                .setSubject("test@example.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hora
                .signWith(strongKey)
                .compact();

        String username = jwtUtils.getAuthenticatedUsername(token);
        assertEquals("test@example.com", username, "El nombre de usuario autenticado debe coincidir con el token.");
    }

    @Test
    public void testValidateToken_EmptyToken() {
        String token = "";

        assertFalse(jwtUtils.validateToken(token), "El token vacío debe ser inválido.");
    }
}
