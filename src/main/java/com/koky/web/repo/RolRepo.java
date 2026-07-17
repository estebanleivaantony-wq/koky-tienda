package com.koky.web.repo; // Paquete actualizado

import org.springframework.data.jpa.repository.JpaRepository;

import com.koky.web.domain.Rol;

import java.util.Optional;

public interface RolRepo extends JpaRepository<Rol, Long> {
    Optional<Rol> findByName(String name); // Método para buscar un rol por su nombre
}