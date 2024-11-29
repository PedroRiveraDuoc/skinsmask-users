package com.duocuc.backend_srv.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.duocuc.backend_srv.security.JwtAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

  private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    logger.info("Configuring security filter chain for API REST...");

    http
        .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Configurar CORS
        .csrf(csrf -> csrf.disable()) // Desactivar CSRF para API REST
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/**").permitAll() // Permitir acceso a las rutas de autenticación
            .requestMatchers(HttpMethod.PUT, "/api/users/**").authenticated() // Asegurar que PUT esté permitido
            .anyRequest().authenticated() // Requerir autenticación para el resto de las rutas
        )
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    // Agregar el filtro JWT antes del UsernamePasswordAuthenticationFilter
    http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    logger.info("Security filter chain configured successfully for API REST.");
    return http.build();
  }

  @Bean
  public CorsFilter corsFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();

    config.setAllowedOrigins(List.of("http://localhost:4200")); // Cambia esta URL según el dominio de tu frontend
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
    config.setAllowCredentials(true);

    source.registerCorsConfiguration("/**", config);
    return new CorsFilter(source);
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
      throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  private UrlBasedCorsConfigurationSource corsConfigurationSource() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();

    config.setAllowedOrigins(List.of("http://localhost:4200")); // Cambia esta URL según el dominio de tu frontend
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
    config.setAllowCredentials(true);

    source.registerCorsConfiguration("/**", config);
    return source;
  }
}
