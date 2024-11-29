package com.duocuc.backend_srv.service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import com.duocuc.backend_srv.model.Role;
import com.duocuc.backend_srv.repository.RoleRepository;

@Service
public class RoleInitializer {

  private final RoleRepository roleRepository;

  public RoleInitializer(RoleRepository roleRepository) {
    this.roleRepository = roleRepository;
  }

  @PostConstruct
  public void initRoles() {
    if (roleRepository.findByCode("ROLE_ADMIN").isEmpty()) {
      roleRepository.save(new Role("ADMIN", "ROLE_ADMIN"));
    }
    if (roleRepository.findByCode("ROLE_USER").isEmpty()) {
      roleRepository.save(new Role("USER", "ROLE_USER"));
    }
  }
}
