package com.koky.web.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // IMPORTANTE
import org.springframework.data.repository.query.Param; // IMPORTANTE
import com.koky.web.domain.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepo extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);

    // --- MÉTODOS OBLIGATORIOS PARA EL ADMIN ---

    // Para la tabla de órdenes
    List<Order> findAllByOrderByOrderDateDesc();

    // Para la tarjeta de ventas (Suma total)
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.orderDate >= :startDate")
    BigDecimal sumTotalSalesAfter(@Param("startDate") LocalDateTime startDate);

    // Para el contador de órdenes
    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderDate >= :startDate")
    long countOrdersAfter(@Param("startDate") LocalDateTime startDate);
}