package com.koky.web.service;

import com.koky.web.domain.Cart;
import com.koky.web.domain.CartItem;
import com.koky.web.domain.Usuario;
import com.koky.web.dto.CartItemDto;
import com.koky.web.dto.CartRequest;
import com.koky.web.repo.CartItemRepo;
import com.koky.web.repo.CartRepo;
import com.koky.web.repo.UsuarioRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {
    private final CartRepo cartRepo;
    private final CartItemRepo cartItemRepo;
    private final UsuarioRepo usuarioRepo;

    public CartService(CartRepo cartRepo, CartItemRepo cartItemRepo, UsuarioRepo usuarioRepo) {
        this.cartRepo = cartRepo;
        this.cartItemRepo = cartItemRepo;
        this.usuarioRepo = usuarioRepo;
    }

    @Transactional
    public Cart getOrCreateCart(Long userId) {
        Usuario user = usuarioRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));

        return cartRepo.findByUser_Id(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart(user);
                    return cartRepo.save(newCart);
                });
    }

    @Transactional
    public CartItemDto addOrUpdateCartItem(CartRequest request) {
        Cart cart = getOrCreateCart(request.getUserId());

        // Usamos el nuevo método con productId
        Optional<CartItem> existingItem = cartItemRepo.findByCartAndProductId(cart, request.getProductId());

        CartItem cartItem;
        if (existingItem.isPresent()) {
            cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        } else {
            cartItem = new CartItem(
                    cart,
                    request.getProductId(), // ID del producto
                    request.getName(),
                    request.getImage_url(),
                    request.getPrice(),
                    request.getQuantity());
            cart.addCartItem(cartItem);
        }
        cartItemRepo.save(cartItem);
        return convertToDto(cartItem);
    }

    public List<CartItemDto> getCartItems(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return cart.getItems().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void removeCartItem(Long itemId, Long userId) {
        Cart cart = getOrCreateCart(userId);
        CartItem itemToRemove = cartItemRepo.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Ítem de carrito no encontrado con ID: " + itemId));

        if (!itemToRemove.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("El ítem no pertenece a este carrito.");
        }
        cartItemRepo.delete(itemToRemove);
        cart.removeCartItem(itemToRemove);
    }

    @Transactional
    public CartItemDto updateCartItemQuantity(Long itemId, Integer newQuantity, Long userId) {
        if (newQuantity <= 0) {
            removeCartItem(itemId, userId);
            return null;
        }

        Cart cart = getOrCreateCart(userId);
        CartItem cartItem = cartItemRepo.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Ítem de carrito no encontrado con ID: " + itemId));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("El ítem no pertenece a este carrito.");
        }

        cartItem.setQuantity(newQuantity);
        cartItemRepo.save(cartItem);
        return convertToDto(cartItem);
    }

    // --- ESTE ES EL MÉTODO QUE TE FALTABA ---
    @Transactional
    public void clearCart(Long userId) {
        Optional<Cart> optionalCart = cartRepo.findByUser_Id(userId);
        if (optionalCart.isPresent()) {
            Cart cart = optionalCart.get();
            // Borra todos los items asociados
            cartItemRepo.deleteAll(cart.getItems());
            // Limpia la lista en memoria
            cart.getItems().clear();
            // Guarda el carrito vacío
            cartRepo.save(cart);
        }
    }

    private CartItemDto convertToDto(CartItem cartItem) {
        return new CartItemDto(
                cartItem.getId(),
                cartItem.getProductId(), // productId
                cartItem.getName(),
                cartItem.getImage_url(),
                cartItem.getPrice(),
                cartItem.getQuantity());
    }
}