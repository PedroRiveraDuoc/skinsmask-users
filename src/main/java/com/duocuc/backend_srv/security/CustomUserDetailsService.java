package com.duocuc.backend_srv.security;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.duocuc.backend_srv.model.User;
import com.duocuc.backend_srv.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  @Autowired
  private UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

    // Convertir roles a GrantedAuthority
    return new org.springframework.security.core.userdetails.User(
        user.getEmail(),
        user.getPassword(),
        user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority(role.getCode()))
            .collect(Collectors.toSet())
    );
  }
}
