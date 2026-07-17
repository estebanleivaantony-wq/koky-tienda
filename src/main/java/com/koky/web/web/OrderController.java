package com.koky.web.web;

import com.koky.web.domain.Order;
import com.koky.web.dto.AdminDashboardResponse; // <--- ESTE ES CRÍTICO
import com.koky.web.dto.OrderResponse;
import com.koky.web.repo.OrderRepo;
import com.koky.web.repo.UsuarioRepo;
import com.koky.web.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Permite que el HTML acceda sin bloqueos
public class OrderController {

    private final OrderService orderService;
    private final OrderRepo orderRepo; // Necesario para datos reales
    private final UsuarioRepo usuarioRepo; // Necesario para contar usuarios

    // Inyección de dependencias en el constructor
    public OrderController(OrderService orderService, OrderRepo orderRepo, UsuarioRepo usuarioRepo) {
        this.orderService = orderService;
        this.orderRepo = orderRepo;
        this.usuarioRepo = usuarioRepo;
    }

    // ==========================================
    // ENDPOINTS EXISTENTES (CLIENTE)
    // ==========================================

    @GetMapping("/orders/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getUserOrders(@PathVariable Long userId) {
        try {
            List<OrderResponse> orders = orderService.getUserOrders(userId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");

        if (userId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "El ID de usuario es obligatorio"));
        }

        try {
            OrderResponse order = orderService.processPaymentAndCreateOrder(userId, "Tarjeta de Crédito");
            return ResponseEntity.ok(Map.of(
                    "orderId", order.getOrderId(),
                    "total", order.getTotalAmount(),
                    "items", order.getItems()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==========================================
    // NUEVOS ENDPOINTS PARA EL ADMIN (REALES)
    // ==========================================

    @GetMapping("/admin/stats")
    public ResponseEntity<AdminDashboardResponse> getAdminStats() {
        // 1. Calcular inicio del día (00:00 AM)
        LocalDateTime inicioDia = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);

        // 2. Consultas a la Base de Datos
        long totalUsuarios = usuarioRepo.count();
        BigDecimal ventasHoy = orderRepo.sumTotalSalesAfter(inicioDia);

        // Si no hay ventas, devuelve 0 en vez de null
        if (ventasHoy == null)
            ventasHoy = BigDecimal.ZERO;

        long ordenesHoy = orderRepo.countOrdersAfter(inicioDia);

        // 3. Devolver respuesta
        AdminDashboardResponse stats = new AdminDashboardResponse(ventasHoy, ordenesHoy, totalUsuarios);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/admin/orders")
    public ResponseEntity<List<OrderResponse>> getAdminOrders() {
        // 1. Obtener órdenes de la BD (Método que agregamos a OrderRepo)
        List<Order> ordenes = orderRepo.findAllByOrderByOrderDateDesc();

        // 2. Convertir a DTO y limitar a las últimas 10
        List<OrderResponse> response = ordenes.stream()
                .limit(10)
                .map(order -> orderService.convertToOrderResponse(order))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}