package com.koky.web.dto;

public class PaymentRequest {
    private Long userId;
    private String paymentMethod; // Ej: "Tarjeta de Crédito", "PayPal"
    private String cardDetails; // En un sistema real, esto sería más complejo (token, etc.)

    // Getters y Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getCardDetails() {
        return cardDetails;
    }

    public void setCardDetails(String cardDetails) {
        this.cardDetails = cardDetails;
    }
}