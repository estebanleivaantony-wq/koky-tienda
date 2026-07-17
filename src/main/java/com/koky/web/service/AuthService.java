package com.koky.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.koky.web.domain.Rol;
import com.koky.web.domain.Usuario;
import com.koky.web.dto.LoginRequest;
import com.koky.web.dto.RegisterRequest;
import com.koky.web.repo.RolRepo;
import com.koky.web.repo.UsuarioRepo;

import java.util.Collections;
import java.util.List; // Asegúrate de tener esta importación
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepo usuarioRepo;
    @Autowired
    private RolRepo rolRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UsuarioDetailsService usuarioDetailsService;

    @Transactional
    public Usuario authenticate(LoginRequest loginRequest) throws AuthenticationException {
        UserDetails userDetails = usuarioDetailsService.loadUserByUsername(loginRequest.getEmail());
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                loginRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return usuarioRepo.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado en DB."));
    }

    @Transactional
    public Usuario register(RegisterRequest registerRequest) {
        if (usuarioRepo.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya está registrado.");
        }

        Usuario newUser = new Usuario();
        newUser.setName(registerRequest.getName());
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        newUser.setPoints(0);

        Rol userRole = rolRepo.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Rol 'USER' no encontrado. Por favor, inicializa los roles."));
        // *** ESTA LÍNEA AHORA ES CORRECTA ya que Usuario.roles es List<Rol> ***
        newUser.setRoles(Collections.singletonList(userRole)); // Asigna una lista inmutable con el rol USER

        return usuarioRepo.save(newUser);
    }

    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepo.findByEmail(email);
    }
}