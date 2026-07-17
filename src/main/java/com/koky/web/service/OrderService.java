package com.koky.web.service;

import com.koky.web.domain.Cart;
import com.koky.web.domain.CartItem;
import com.koky.web.domain.Order;
import com.koky.web.domain.OrderItem;
import com.koky.web.domain.Usuario; // Importación necesaria
import com.koky.web.dto.OrderItemDto;
import com.koky.web.dto.OrderResponse;
import com.koky.web.repo.CartRepo;
import com.koky.web.repo.OrderRepo;
import com.koky.web.repo.UsuarioRepo; // Importación necesaria
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private CartService cartService;

    // --- INYECCIÓN NECESARIA PARA ACTUALIZAR PUNTOS ---
    @Autowired
    private UsuarioRepo usuarioRepo;

    @Transactional
    public OrderResponse processPaymentAndCreateOrder(Long userId, String paymentMethod) {
        Cart userCart = cartRepo.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado para el usuario: " + userId));

        if (userCart.getItems().isEmpty()) {
            throw new RuntimeException("El carrito del usuario está vacío.");
        }

        BigDecimal totalAmount = userCart.getItems().stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        boolean paymentSuccessful = simulatePayment(totalAmount, paymentMethod);

        if (!paymentSuccessful) {
            throw new RuntimeException("El procesamiento de pago falló. Inténtelo de nuevo.");
        }

        // ==========================================================
        // === NUEVO: LÓGICA DE PUNTOS (1 Sol = 1 Punto) ===
        // ==========================================================
        Usuario user = userCart.getUser();
        int pointsEarned = totalAmount.intValue(); // Redondeo hacia abajo
        int currentPoints = user.getPoints() == null ? 0 : user.getPoints();

        user.setPoints(currentPoints + pointsEarned);
        usuarioRepo.save(user); // Guardamos los nuevos puntos en la BD
        // ==========================================================

        Order newOrder = new Order();
        newOrder.setUserId(userId);
        newOrder.setOrderDate(LocalDateTime.now());
        newOrder.setTotalAmount(totalAmount);
        newOrder.setPaymentMethod(paymentMethod);

        // *** CAMBIO: Estado inicial ahora es PENDING (En Proceso) ***
        newOrder.setStatus("PENDING");

        newOrder.setReceiptNumber("KOKY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        for (CartItem cartItem : userCart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setName(cartItem.getName());
            orderItem.setImage_url(cartItem.getImage_url());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());
            newOrder.addItem(orderItem);
        }

        Order savedOrder = orderRepo.save(newOrder);
        cartService.clearCart(userId);
        return convertToOrderResponse(savedOrder);
    }

    private boolean simulatePayment(BigDecimal amount, String paymentMethod) {
        return amount.compareTo(BigDecimal.ZERO) > 0 && !"FALLO_SIMULADO".equals(paymentMethod);
    }

    public OrderResponse convertToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getId());
        response.setReceiptNumber(order.getReceiptNumber());
        response.setOrderDate(order.getOrderDate());
        response.setTotalAmount(order.getTotalAmount());
        response.setPaymentMethod(order.getPaymentMethod());
        response.setStatus(order.getStatus());

        List<OrderItemDto> itemDtos = order.getItems().stream()
                .map(item -> {
                    OrderItemDto itemDto = new OrderItemDto();
                    itemDto.setProductId(item.getProductId());
                    itemDto.setName(item.getName());
                    itemDto.setImage_url(item.getImage_url());
                    itemDto.setQuantity(item.getQuantity());
                    itemDto.setPrice(item.getPrice());
                    return itemDto;
                })
                .collect(Collectors.toList());
        response.setItems(itemDtos);
        return response;
    }

    public List<OrderResponse> getUserOrders(Long userId) {
        List<Order> orders = orderRepo.findByUserId(userId);
        return orders.stream()
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());
    }
}