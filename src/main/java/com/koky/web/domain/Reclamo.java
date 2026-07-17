package com.koky.web.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reclamos")
public class Reclamo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String codigo; // Ej: REC-2025-001

    private Long userId; // Opcional: si el usuario estaba logueado
    private String nombre;
    private String email;
    private String telefono;
    private String tipo; // "RECLAMO" (Producto) o "QUEJA" (Atención)

    @Lob
    private String detalle;

    private String estado; // "PENDIENTE", "ATENDIDO"
    private LocalDateTime fecha;

    public Reclamo() {
        this.fecha = LocalDateTime.now();
        this.estado = "PENDIENTE";
    }

    // Constructor con datos
    public Reclamo(Long userId, String nombre, String email, String telefono, String tipo, String detalle) {
        this.userId = userId;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.tipo = tipo;
        this.detalle = detalle;
        this.fecha = LocalDateTime.now();
        this.estado = "PENDIENTE";
    }

    // Getters y Setters (Necesarios)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}