package com.koky.web.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.koky.web.domain.Producto;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepo extends JpaRepository<Producto, Long> {

    // Buscar por nombre
    Optional<Producto> findByName(String name);

    // Buscar para la barra de búsqueda
    List<Producto> findByNameContainingIgnoreCase(String name);

    // Buscar por categoría (Escolar, Oficina, etc.)
    List<Producto> findByCategory(String category);
}