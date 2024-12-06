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
  @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
  @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores")
  private String username;

  @Column(nullable = false)
  @NotBlank(message = "Password is mandatory")
  private String password;

  @Column(unique = true, nullable = false)
  @NotBlank(message = "Email is mandatory")
  @Email(message = "Invalid email format")
  private String email;

  @Column(nullable = false)
  @NotBlank(message = "First name is mandatory")
  @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
  private String firstName;

  @Column(nullable = false)
  @NotBlank(message = "Last name is mandatory")
  @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
  private String lastName;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
  @JsonIgnore
  private Set<Role> roles = new HashSet<>();

  // Constructor sin argumentos (necesario para JPA)
  public User() {
  }

  // Constructor con todos los campos (sin incluir id ni roles)
  public User(String username, String password, String email, String firstName, String lastName) {
    this.username = username;
    this.password = password;
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
  }

  // Getters y Setters

  public Long getId(){
    return id;
  }

  public void setId(Long id){
    this.id = id;
  }

  public String getUsername(){
    return username;
  }

  public void setUsername(String username){
    this.username = username;
  }

  public String getPassword(){
    return password;
  }

  public void setPassword(String password){
    this.password = password;
  }

  public String getEmail(){
    return email;
  }

  public void setEmail(String email){
    this.email = email;
  }

  public String getFirstName(){
    return firstName;
  }

  public void setFirstName(String firstName){
    this.firstName = firstName;
  }

  public String getLastName(){
    return lastName;
  }

  public void setLastName(String lastName){
    this.lastName = lastName;
  }

  public Set<Role> getRoles(){
    return roles;
  }

  public void setRoles(Set<Role> roles){
    this.roles = roles;
  }

  // Método para agregar un rol al usuario
  public void addRole(Role role){
    this.roles.add(role);
  }

  // Método para eliminar un rol del usuario
  public void removeRole(Role role){
    this.roles.remove(role);
  }

  // toString (opcional, para facilitar la depuración)
  @Override
  public String toString(){
    return "User{" +
        "id=" + id +
        ", username='" + username + '\'' +
        ", email='" + email + '\'' +
        ", firstName='" + firstName + '\'' +
        ", lastName='" + lastName + '\'' +
        ", roles=" + roles +
        '}';
  }
}
