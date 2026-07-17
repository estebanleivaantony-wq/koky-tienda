package com.koky.web.domain;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String lastName;

    @Column(unique = true)
    private String email;

    private String password;
    private Integer points;

    // === NUEVO CAMPO PARA RECUPERAR CONTRASEÑA ===
    @Column(name = "reset_token")
    private String resetToken;
    // =============================================

    // --- RELACIÓN CON ROLES ---
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "usuario_roles", joinColumns = @JoinColumn(name = "usuario_id"), inverseJoinColumns = @JoinColumn(name = "rol_id"))
    private List<Rol> roles = new ArrayList<>();

    // --- RELACIÓN CON WISHLIST ---
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "usuario_wishlist", joinColumns = @JoinColumn(name = "usuario_id"), inverseJoinColumns = @JoinColumn(name = "producto_id"))
    private List<Producto> wishlist = new ArrayList<>();

    // --- Constructores ---
    public Usuario() {
    }

    public Usuario(String name, String lastName, String email, String password, Integer points) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.points = points;
    }

    // --- Métodos Helper para Wishlist ---
    public void addToWishlist(Producto producto) {
        if (!this.wishlist.contains(producto)) {
            this.wishlist.add(producto);
        }
    }

    public void removeFromWishlist(Producto producto) {
        this.wishlist.remove(producto);
    }

    // --- Getters y Setters ---
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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    // === GETTER Y SETTER DEL TOKEN ===
    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }
    // =================================

    public List<Rol> getRoles() {
        return roles;
    }

    public void setRoles(List<Rol> roles) {
        this.roles = roles;
    }

    public List<Producto> getWishlist() {
        return wishlist;
    }

    public void setWishlist(List<Producto> wishlist) {
        this.wishlist = wishlist;
    }

    // --- Métodos de Spring Security (UserDetails) ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}