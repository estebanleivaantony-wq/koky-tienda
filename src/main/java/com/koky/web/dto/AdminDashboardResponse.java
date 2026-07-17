package com.koky.web.dto;

import java.math.BigDecimal;

public class AdminDashboardResponse {
    private BigDecimal ventasHoy;
    private long ordenesHoy;
    private long usuariosTotales;
    private String estadoSistema;
    private String estadoBaseDatos;

    public AdminDashboardResponse(BigDecimal ventasHoy, long ordenesHoy, long usuariosTotales) {
        this.ventasHoy = ventasHoy;
        this.ordenesHoy = ordenesHoy;
        this.usuariosTotales = usuariosTotales;
        this.estadoSistema = "EN LÍNEA";
        this.estadoBaseDatos = "CONECTADA";
    }

    // Getters y Setters obligatorios
    public BigDecimal getVentasHoy() {
        return ventasHoy;
    }

    public void setVentasHoy(BigDecimal ventasHoy) {
        this.ventasHoy = ventasHoy;
    }

    public long getOrdenesHoy() {
        return ordenesHoy;
    }

    public void setOrdenesHoy(long ordenesHoy) {
        this.ordenesHoy = ordenesHoy;
    }

    public long getUsuariosTotales() {
        return usuariosTotales;
    }

    public void setUsuariosTotales(long usuariosTotales) {
        this.usuariosTotales = usuariosTotales;
    }

    public String getEstadoSistema() {
        return estadoSistema;
    }

    public String getEstadoBaseDatos() {
        return estadoBaseDatos;
    }
}