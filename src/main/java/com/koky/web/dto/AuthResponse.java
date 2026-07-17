package com.koky.web.dto;

public class AuthResponse {
    private String username;
    private Long userId; // Asegúrate de que esto existe
    private String token; // ¡¡¡Esta propiedad es CRÍTICA!!!
    private String email;
    private String lastName;
    private Integer points;

    // Constructor, getters y setters
    public AuthResponse(String username, Long userId, String token, String email, String lastName, Integer points) {
        this.username = username;
        this.userId = userId;
        this.token = token; // Asegúrate de que el token se asigne aquí
        this.email = email;
        this.lastName = lastName;
        this.points = points;
    }

    // Getters y Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    } // ¡¡¡Necesario para el frontend!!!

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }
}