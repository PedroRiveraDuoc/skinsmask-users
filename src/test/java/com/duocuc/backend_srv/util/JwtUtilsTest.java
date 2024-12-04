package com.duocuc.backend_srv.util;

import com.duocuc.backend_srv.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import jakarta.servlet.http.HttpServletRequest;

import java.security.Key;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @Mock
    private JwtConfig jwtConfig;

    @Mock
    private HttpServletRequest request;

    private Key strongKey;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Secure key setup
        strongKey = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS512);
        String encodedKey = Base64.getEncoder().encodeToString(strongKey.getEncoded());

        when(jwtConfig.getSecret()).thenReturn(encodedKey);
        when(jwtConfig.getExpirationMs()).thenReturn(3600000); // 1 hour in milliseconds

        jwtUtils = new JwtUtils(jwtConfig);
    }

    @Test
    public void testGenerateJwtToken() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@example.com",
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        String token = jwtUtils.generateJwtToken(authentication);

        assertNotNull(token, "Token should not be null.");
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(strongKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals("test@example.com", claims.getSubject(), "Subject should match the email.");
        assertEquals("ROLE_USER", claims.get("roles"), "Roles claim should match.");
    }

    @Test
    public void testGetSigningKey() {
        Key key = jwtUtils.getSigningKey();
        assertNotNull(key, "Signing key should not be null.");
        assertEquals(strongKey, key, "Signing key should match the configured key.");
    }

    @Test
    public void testGetSigningKey_InvalidSecret() {
        // Provide an invalid secret (less than 256 bits)
        when(jwtConfig.getSecret()).thenReturn("short");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> jwtUtils.getSigningKey());

        // Assert the actual exception message from the library
        assertEquals("Last unit does not have enough valid bits", exception.getMessage());
    }

    @Test
    public void testValidateToken_ValidToken() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@example.com",
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        String token = jwtUtils.generateJwtToken(authentication);
        assertTrue(jwtUtils.validateToken(token), "Valid token should pass validation.");
    }

    @Test
    public void testValidateToken_ExpiredToken() {
        String expiredToken = Jwts.builder()
                .setSubject("test@example.com")
                .setIssuedAt(new Date(System.currentTimeMillis() - 7200000)) // 2 hours ago
                .setExpiration(new Date(System.currentTimeMillis() - 3600000)) // 1 hour ago
                .signWith(strongKey)
                .compact();

        assertFalse(jwtUtils.validateToken(expiredToken), "Expired token should fail validation.");
    }

    @Test
    public void testValidateToken_NullToken() {
        assertFalse(jwtUtils.validateToken(null), "Null token should fail validation.");
    }

    @Test
    public void testValidateToken_MalformedToken() {
        String malformedToken = "ThisIsNotAJwtToken";

        assertFalse(jwtUtils.validateToken(malformedToken), "Malformed token should fail validation.");
    }

    @Test
    public void testValidateToken_SignatureMismatch() {
        String otherKeyEncoded = Base64.getEncoder()
                .encodeToString(Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS512).getEncoded());
        Key otherKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(otherKeyEncoded));

        String token = Jwts.builder()
                .setSubject("test@example.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(otherKey)
                .compact();

        assertFalse(jwtUtils.validateToken(token), "Token signed with a different key should fail validation.");
    }

    @Test
    public void testGetJwtFromRequest() {
        when(request.getHeader("Authorization")).thenReturn("Bearer ValidToken");

        String token = jwtUtils.getJwtFromRequest(request);
        assertNotNull(token, "Token should not be null when Authorization header is present.");
        assertEquals("ValidToken", token, "Token should be extracted correctly from the header.");
    }

    @Test
    public void testGetJwtFromRequest_MissingHeader() {
        when(request.getHeader("Authorization")).thenReturn(null);

        String token = jwtUtils.getJwtFromRequest(request);
        assertNull(token, "Token should be null when Authorization header is missing.");
    }

    @Test
    public void testGetJwtFromRequest_InvalidHeader() {
        when(request.getHeader("Authorization")).thenReturn("InvalidHeaderFormat");

        String token = jwtUtils.getJwtFromRequest(request);
        assertNull(token, "Token should be null when Authorization header format is invalid.");
    }

    @Test
    public void testGetAuthenticatedUsername() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@example.com",
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        String token = jwtUtils.generateJwtToken(authentication);

        String username = jwtUtils.getAuthenticatedUsername(token);
        assertNotNull(username, "Username should not be null for a valid token.");
        assertEquals("test@example.com", username, "Username should match the subject in the token.");
    }

    @Test
    public void testGetAuthenticatedUsername_InvalidToken() {
        String invalidToken = "InvalidToken";

        Exception exception = assertThrows(io.jsonwebtoken.JwtException.class,
                () -> jwtUtils.getAuthenticatedUsername(invalidToken));
        assertTrue(exception.getMessage().contains("JWT"), "Exception should indicate an issue with the JWT.");
    }
}
