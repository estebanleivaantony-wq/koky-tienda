package com.koky.web.web;

import com.koky.web.dto.CartItemDto;
import com.koky.web.dto.CartRequest;
import com.koky.web.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // GET /api/cart/current?userId=1
    @GetMapping("/current")
    public ResponseEntity<List<CartItemDto>> getCart(@RequestParam Long userId) {
        List<CartItemDto> items = cartService.getCartItems(userId);
        return ResponseEntity.ok(items);
    }

    // POST /api/cart/add
    @PostMapping("/add")
    public ResponseEntity<CartItemDto> addToCart(@RequestBody CartRequest request) {
        CartItemDto item = cartService.addOrUpdateCartItem(request);
        return ResponseEntity.ok(item);
    }
}