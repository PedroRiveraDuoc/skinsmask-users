package com.duocuc.backend_srv.security;

import com.duocuc.backend_srv.util.JwtUtils;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JwtAuthenticationFilterTest {

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private Claims claims;

    private final String jwtToken = "test.jwt.token";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtils);
    }

    @Test
    public void testDoFilterInternal_ValidToken() throws ServletException, IOException {
        // Arrange
        when(jwtUtils.getJwtFromRequest(request)).thenReturn(jwtToken);
        when(jwtUtils.validateToken(jwtToken)).thenReturn(true);
        when(jwtUtils.getClaimsFromToken(jwtToken)).thenReturn(claims);
        when(claims.getSubject()).thenReturn("testUser");
        when(claims.get("roles")).thenReturn("ROLE_USER,ROLE_ADMIN");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication, "Authentication should not be null.");
        assertEquals("testUser", authentication.getPrincipal(), "Principal should match the username.");
        assertTrue(
                authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")),
                "Authorities should contain ROLE_USER."
        );
        assertTrue(
                authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")),
                "Authorities should contain ROLE_ADMIN."
        );
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    public void testDoFilterInternal_MissingToken() throws ServletException, IOException {
        // Arrange
        when(jwtUtils.getJwtFromRequest(request)).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication(), "Authentication should be null when token is missing.");
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    public void testDoFilterInternal_NullRoles() throws ServletException, IOException {
        // Arrange
        when(jwtUtils.getJwtFromRequest(request)).thenReturn(jwtToken);
        when(jwtUtils.validateToken(jwtToken)).thenReturn(true);
        when(jwtUtils.getClaimsFromToken(jwtToken)).thenReturn(claims);
        when(claims.getSubject()).thenReturn("testUser");
        when(claims.get("roles")).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication, "Authentication should not be null.");
        assertEquals("testUser", authentication.getPrincipal(), "Principal should match the username.");
        assertTrue(authentication.getAuthorities().isEmpty(), "Authorities should be empty when roles are null.");
        verify(filterChain, times(1)).doFilter(request, response);
    }
}
