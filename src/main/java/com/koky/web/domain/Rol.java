package com.koky.web.domain;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority; // <<< Asegúrate de que esta importación es correcta

import java.util.Objects;

@Entity
@Table(name = "roles")
public class Rol implements GrantedAuthority { // <<< ¡¡¡CRÍTICO: Implementa GrantedAuthority aquí!!!
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name; // Ej: "ADMIN", "USER"

    public Rol() {
    }

    public Rol(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // *** IMPLEMENTACIÓN DEL MÉTODO getAuthority() ***
    @Override
    public String getAuthority() {
        return name; // Devuelve el nombre del rol como la autoridad
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Rol rol = (Rol) o;
        return Objects.equals(id, rol.id) && Objects.equals(name, rol.name); // Comparar por ID y nombre
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name); // Hash por ID y nombre
    }
}