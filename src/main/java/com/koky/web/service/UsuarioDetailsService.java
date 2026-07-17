package com.koky.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User; // Importación para el objeto User de Spring Security
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.koky.web.domain.Usuario;
import com.koky.web.repo.UsuarioRepo;

import java.util.stream.Collectors;

@Service
public class UsuarioDetailsService implements UserDetailsService {

        @Autowired
        private UsuarioRepo usuarioRepo;

        public UsuarioDetailsService(UsuarioRepo usuarioRepo) {
                this.usuarioRepo = usuarioRepo;
        }

        @Override
        public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
                // Busca el usuario en tu repositorio por email (que es tu "username")
                Usuario usuario = usuarioRepo.findByEmail(email)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                "Usuario no encontrado con email: " + email));

                // Convierte tu entidad Usuario a un objeto UserDetails de Spring Security
                // Asume que tu entidad Rol implementa GrantedAuthority
                return new User(
                                usuario.getEmail(),
                                usuario.getPassword(),
                                usuario.getRoles().stream()
                                                .map(rol -> (org.springframework.security.core.GrantedAuthority) rol)
                                                .collect(Collectors.toList()));
        }
}