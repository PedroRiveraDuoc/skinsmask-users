package com.duocuc.backend_srv.util;

import com.duocuc.backend_srv.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import jakarta.servlet.http.HttpServletRequest;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

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
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Generate a strong key
        strongKey = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS512);
        String encodedKey = Base64.getEncoder().encodeToString(strongKey.getEncoded());

        when(jwtConfig.getSecret()).thenReturn(encodedKey);
        when(jwtConfig.getExpirationMs()).thenReturn((int) 3600000L); // 1 hour in milliseconds

        jwtUtils = new JwtUtils(jwtConfig);
    }

    @Test
    public void testValidateToken_ExpiredToken() {
        String expiredToken = Jwts.builder()
                .setSubject("test@example.com")
                .setIssuedAt(new Date(System.currentTimeMillis() - 3600000L)) // 1 hour ago
                .setExpiration(new Date(System.currentTimeMillis() - 1800000L)) // 30 minutes ago
                .signWith(strongKey)
                .compact();

        boolean isValid = jwtUtils.validateToken(expiredToken);
        assertFalse(isValid, "The token should be invalid because it is expired.");
    }


    @Test
    public void testGetJwtFromRequest_ValidHeader() {
        when(request.getHeader("Authorization")).thenReturn("Bearer my-jwt-token");

        String token = jwtUtils.getJwtFromRequest(request);
        assertEquals("my-jwt-token", token, "The extracted token should match the value in the Authorization header.");
    }

    @Test
    public void testGetJwtFromRequest_NoHeader() {
        when(request.getHeader("Authorization")).thenReturn(null);

        String token = jwtUtils.getJwtFromRequest(request);
        assertNull(token, "The token should be null when the Authorization header is missing.");
    }

}
