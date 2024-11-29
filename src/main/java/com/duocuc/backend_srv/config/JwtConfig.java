package com.duocuc.backend_srv.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.expirationMs}")
  private int expirationMs;

  public String getSecret() {
    return secret;
  }

  public int getExpirationMs() {
    return expirationMs;
  }
}
