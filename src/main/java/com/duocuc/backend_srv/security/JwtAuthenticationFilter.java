package com.duocuc.backend_srv.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.duocuc.backend_srv.util.JwtUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String jwtToken = jwtUtils.getJwtFromRequest(request);
        if (jwtToken != null && jwtUtils.validateToken(jwtToken)) {
            Claims claims = jwtUtils.getClaimsFromToken(jwtToken);
            String username = claims.getSubject();
            System.out.println("Extracted username from JWT: " + username); 

            // Obtener roles del JWT y manejar caso donde roles sea null
            String roles = (String) claims.get("roles");
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();

            if (roles != null) {
                authorities = List.of(roles.split(",")).stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
            }

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    username, null, authorities);
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        chain.doFilter(request, response);
    }

}
