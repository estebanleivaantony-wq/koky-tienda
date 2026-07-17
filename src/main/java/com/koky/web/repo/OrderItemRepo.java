package com.koky.web.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.koky.web.domain.OrderItem;

public interface OrderItemRepo extends JpaRepository<OrderItem, Long> {
}