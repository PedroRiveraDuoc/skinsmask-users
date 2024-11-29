package com.duocuc.backend_srv.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.duocuc.backend_srv.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByCode(String code);
}