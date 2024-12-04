package com.duocuc.backend_srv.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  @NotBlank(message = "Username is mandatory")
  private String username;

  @Column(nullable = false)
  @NotBlank(message = "Password is mandatory")
  @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$", message = "Password must be 8-20 characters long, include letters, numbers, and at least one special character")
  private String password;

  @Column(unique = true, nullable = false)
  @NotBlank(message = "Email is mandatory")
  @Email(message = "Invalid email format")
  private String email;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
  @JsonIgnore
  private Set<Role> roles = new HashSet<>();

  // Constructor sin argumentos (necesario para JPA)
  public User() {
  }

  // Constructor con todos los campos (sin incluir id ni roles)
  public User(String username, String password, String email) {
    this.username = username;
    this.password = password;
    this.email = email;
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

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Set<Role> getRoles() {
    return roles;
  }

  public void setRoles(Set<Role> roles) {
    this.roles = roles;
  }

  // Método para agregar un rol al usuario
  public void addRole(Role role) {
    this.roles.add(role);
  }

  // Método para eliminar un rol del usuario
  public void removeRole(Role role) {
    this.roles.remove(role);
  }

  // toString (opcional, para facilitar la depuración)
  @Override
  public String toString() {
    return "User{" +
        "id=" + id +
        ", username='" + username + '\'' +
        ", email='" + email + '\'' +
        ", roles=" + roles +
        '}';
  }
}
