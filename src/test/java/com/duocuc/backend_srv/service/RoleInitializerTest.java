package com.duocuc.backend_srv.service;

import com.duocuc.backend_srv.model.Role;
import com.duocuc.backend_srv.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleInitializerTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleInitializer roleInitializer;

    @BeforeEach
    public void setUp() {
        // No es necesario en este caso ya que usamos @InjectMocks
    }

    // Prueba cuando ambos roles no existen
    @Test
    public void testInitRoles_BothRolesDoNotExist() {
        // Mocks y stubs
        when(roleRepository.findByCode("ROLE_ADMIN")).thenReturn(Optional.empty());
        when(roleRepository.findByCode("ROLE_USER")).thenReturn(Optional.empty());

        // Ejecución
        roleInitializer.initRoles();

        // Verificaciones
        verify(roleRepository, times(1)).findByCode("ROLE_ADMIN");
        verify(roleRepository, times(1)).findByCode("ROLE_USER");
        verify(roleRepository, times(1)).save(new Role("ADMIN", "ROLE_ADMIN"));
        verify(roleRepository, times(1)).save(new Role("USER", "ROLE_USER"));
    }

    // Prueba cuando ROLE_ADMIN existe pero ROLE_USER no
    @Test
    public void testInitRoles_AdminExistsUserDoesNot() {
        // Mocks y stubs
        when(roleRepository.findByCode("ROLE_ADMIN")).thenReturn(Optional.of(new Role()));
        when(roleRepository.findByCode("ROLE_USER")).thenReturn(Optional.empty());

        // Ejecución
        roleInitializer.initRoles();

        // Verificaciones
        verify(roleRepository, times(1)).findByCode("ROLE_ADMIN");
        verify(roleRepository, times(1)).findByCode("ROLE_USER");
        verify(roleRepository, never()).save(new Role("ADMIN", "ROLE_ADMIN"));
        verify(roleRepository, times(1)).save(new Role("USER", "ROLE_USER"));
    }

    // Prueba cuando ROLE_USER existe pero ROLE_ADMIN no
    @Test
    public void testInitRoles_UserExistsAdminDoesNot() {
        // Mocks y stubs
        when(roleRepository.findByCode("ROLE_ADMIN")).thenReturn(Optional.empty());
        when(roleRepository.findByCode("ROLE_USER")).thenReturn(Optional.of(new Role()));

        // Ejecución
        roleInitializer.initRoles();

        // Verificaciones
        verify(roleRepository, times(1)).findByCode("ROLE_ADMIN");
        verify(roleRepository, times(1)).findByCode("ROLE_USER");
        verify(roleRepository, times(1)).save(new Role("ADMIN", "ROLE_ADMIN"));
        verify(roleRepository, never()).save(new Role("USER", "ROLE_USER"));
    }

    // Prueba cuando ambos roles ya existen
    @Test
    public void testInitRoles_BothRolesExist() {
        // Mocks y stubs
        when(roleRepository.findByCode("ROLE_ADMIN")).thenReturn(Optional.of(new Role()));
        when(roleRepository.findByCode("ROLE_USER")).thenReturn(Optional.of(new Role()));

        // Ejecución
        roleInitializer.initRoles();

        // Verificaciones
        verify(roleRepository, times(1)).findByCode("ROLE_ADMIN");
        verify(roleRepository, times(1)).findByCode("ROLE_USER");
        verify(roleRepository, never()).save(any(Role.class));
    }
}
