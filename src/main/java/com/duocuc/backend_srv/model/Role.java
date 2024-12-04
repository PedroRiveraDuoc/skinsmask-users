package com.duocuc.backend_srv.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "roles")
public class Role {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String name;

  @Column(unique = true, nullable = false)
  private String code;

  @ManyToMany(mappedBy = "roles")
  @JsonIgnore
  private Set<User> users = new HashSet<>();

  // Constructor sin parámetros
  public Role() {
  }

  // Constructor con todos los campos
  public Role(String name, String code) {
    this.name = name;
    this.code = code;
  }

  // Getters y Setters
  public Long getId() {
    return id;
  }

  // Generalmente, el setter de 'id' no es necesario si el ID es autogenerado
  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public Set<User> getUsers() {
    return users;
  }

  public void setUsers(Set<User> users) {
    this.users = users;
  }

  // Implementación de equals() y hashCode()
  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    Role role = (Role) o;
    return Objects.equals(name, role.name) && Objects.equals(code, role.code);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, code);
  }

  // toString para depuración
  @Override
  public String toString() {
    return "Role{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", code='" + code + '\'' +
        '}';
  }
}
