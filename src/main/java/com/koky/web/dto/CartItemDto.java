package com.koky.web.dto;

import java.math.BigDecimal;

public class CartItemDto {
    private Long id;
    private Long productId; // Antes gameId
    private String name;
    private String image_url;
    private BigDecimal price;
    private Integer quantity;

    public CartItemDto() {
    }

    public CartItemDto(Long id, Long productId, String name, String image_url, BigDecimal price, Integer quantity) {
        this.id = id;
        this.productId = productId;
        this.name = name;
        this.image_url = image_url;
        this.price = price;
        this.quantity = quantity;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}