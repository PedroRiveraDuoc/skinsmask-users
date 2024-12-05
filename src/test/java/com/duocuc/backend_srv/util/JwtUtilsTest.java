package com.duocuc.backend_srv.util;

import com.duocuc.backend_srv.config.JwtConfig;
import com.duocuc.backend_srv.security.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import jakarta.servlet.http.HttpServletRequest;

import java.security.Key;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @Mock
    private JwtConfig jwtConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(jwtConfig.getSecret()).thenReturn("YXV0aGVudGljYXRpb25TZWNyZXRLZXlTZWNyZXQxMjM0NTY3ODkxMjM0NTY3OA=="); // Base64 encoded 512-bit key
        when(jwtConfig.getExpirationMs()).thenReturn(3600000); // 1 hour
        jwtUtils = new JwtUtils(jwtConfig);
    }

    @Test
    void testGetSigningKey() {
        Key signingKey = jwtUtils.getSigningKey();
        assertNotNull(signingKey);
    }


    @Test
    void testValidateToken_MalformedToken() {
        String malformedToken = "invalid.token.structure";

        assertFalse(jwtUtils.validateToken(malformedToken));
    }

    @Test
    void testGetClaimsFromToken_InvalidToken() {
        String invalidToken = "invalid.token";

        assertThrows(io.jsonwebtoken.JwtException.class, () -> jwtUtils.getClaimsFromToken(invalidToken));
    }

    @Test
    void testGetJwtFromRequest_ValidHeader() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer valid.jwt.token");

        String token = jwtUtils.getJwtFromRequest(request);
        assertEquals("valid.jwt.token", token);
    }

    @Test
    void testGetJwtFromRequest_NoHeader() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn(null);

        String token = jwtUtils.getJwtFromRequest(request);
        assertNull(token);
    }

    @Test
    void testGetJwtFromRequest_InvalidHeader() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("InvalidHeader");

        String token = jwtUtils.getJwtFromRequest(request);
        assertNull(token);
    }
}
