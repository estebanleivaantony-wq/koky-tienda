package com.koky.web.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod; // Ej: "Credit Card", "PayPal"

    @Column(name = "status", nullable = false)
    private String status; // Ej: "PENDING", "COMPLETED", "FAILED"

    // ***************************************************************
    // !!! ESTA ES LA LÍNEA CRÍTICA QUE DEBE SER 'String' !!!
    @Column(name = "receipt_number", unique = true, nullable = false)
    private String receiptNumber; // <--- ¡¡¡DEBE SER 'String' AQUÍ!!!
    // ***************************************************************

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> items = new ArrayList<>();

    // Constructor, Getters y Setters
    public Order() {
        this.orderDate = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // ***************************************************************
    // !!! ESTE GETTER DEBE DEVOLVER 'String' !!!
    public String getReceiptNumber() { // <--- ¡¡¡DEBE DEVOLVER 'String' AQUÍ!!!
        return receiptNumber;
    }
    // ***************************************************************

    // ***************************************************************
    // !!! ESTE SETTER DEBE ACEPTAR 'String' !!!
    public void setReceiptNumber(String receiptNumber) { // <--- ¡¡¡DEBE ACEPTAR 'String' AQUÍ!!!
        this.receiptNumber = receiptNumber;
    }
    // ***************************************************************

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
        for (OrderItem item : items) {
            item.setOrder(this);
        }
    }

    public void addItem(OrderItem item) {
        this.items.add(item);
        item.setOrder(this);
    }
}