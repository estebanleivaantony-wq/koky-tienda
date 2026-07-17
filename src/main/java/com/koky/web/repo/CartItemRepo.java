package com.koky.web.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.koky.web.domain.Cart;
import com.koky.web.domain.CartItem;
import java.util.Optional;

@Repository
public interface CartItemRepo extends JpaRepository<CartItem, Long> {
    // Busca por carrito y PRODUCT ID
    Optional<CartItem> findByCartAndProductId(Cart cart, Long productId);
}