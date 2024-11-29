package com.duocuc.backend_srv.dto;

import java.util.List;

public class UserProfileDto {
  private Long id;
  private String username;
  private List<RoleDto> roles;

  // Constructor
  public UserProfileDto(Long id, String username, List<RoleDto> roles) {
    this.id = id;
    this.username = username;
    this.roles = roles;
  }

  // Getters y Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public List<RoleDto> getRoles() {
    return roles;
  }

  public void setRoles(List<RoleDto> roles) {
    this.roles = roles;
  }
}
