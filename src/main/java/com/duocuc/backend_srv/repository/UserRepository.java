package com.duocuc.backend_srv.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.duocuc.backend_srv.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username); // Esto puede eliminarse si ya no usas username
  Optional<User> findByEmail(String email); // Nueva consulta para email
  boolean existsByUsername(String username); // Esto también puede eliminarse si ya no usas username
  boolean existsByEmail(String email); // Verificar existencia por email
}
