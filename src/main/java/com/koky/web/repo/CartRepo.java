package com.koky.web.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.koky.web.domain.Cart;
import com.koky.web.domain.Usuario;

import java.util.Optional;

@Repository
public interface CartRepo extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(Usuario user); // Encuentra un carrito por su usuario

    // ¡¡¡ESTA ES LA LÍNEA CRÍTICA!!!
    // Busca un Cart por el ID del Usuario asociado a través de la propiedad 'user'
    Optional<Cart> findByUser_Id(Long userId);
}