package com.koky.web.dto;

import java.math.BigDecimal;

public class OrderItemDto {
    private Long productId; // <<< CAMBIO: Antes gameId
    private String name;
    private String image_url;
    private Integer quantity;
    private BigDecimal price;

    // Getters y Setters
    public Long getProductId() {
        return productId;
    } // <<< CAMBIO

    public void setProductId(Long productId) {
        this.productId = productId;
    } // <<< CAMBIO

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}