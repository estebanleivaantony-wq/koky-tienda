package com.koky.web.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Esta es la relación CRÍTICA que define el nombre 'user'
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true) // Asegúrate de que apunte a la columna user_id en la
                                                                   // BD
    private Usuario user; // <-- EL NOMBRE DE ESTA PROPIEDAD ES IMPORTANTE

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CartItem> items = new ArrayList<>();

    // Constructor vacío
    public Cart() {
    }

    // Constructor con Usuario
    public Cart(Usuario user) {
        this.user = user;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUser() { // <-- GETTER CORRESPONDIENTE
        return user;
    }

    public void setUser(Usuario user) {
        this.user = user;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
        for (CartItem item : items) {
            item.setCart(this);
        }
    }

    public void addCartItem(CartItem item) {
        this.items.add(item);
        item.setCart(this);
    }

    public void removeCartItem(CartItem item) {
        this.items.remove(item);
        item.setCart(null);
    }
}