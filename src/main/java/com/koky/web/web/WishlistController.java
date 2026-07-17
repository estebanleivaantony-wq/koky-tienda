package com.koky.web.web;

import com.koky.web.domain.Producto;
import com.koky.web.domain.Usuario;
import com.koky.web.repo.ProductoRepo;
import com.koky.web.repo.UsuarioRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wishlist")
@CrossOrigin(origins = "*") // <<< IMPORTANTE: Permite la conexión con el HTML/JS
public class WishlistController {

    private final UsuarioRepo usuarioRepo;
    private final ProductoRepo productoRepo;

    public WishlistController(UsuarioRepo usuarioRepo, ProductoRepo productoRepo) {
        this.usuarioRepo = usuarioRepo;
        this.productoRepo = productoRepo;
    }

    @GetMapping("/{userId}")
    @Transactional(readOnly = true) // Evita errores de "LazyInitializationException"
    public ResponseEntity<List<Producto>> getWishlist(@PathVariable Long userId) {
        return usuarioRepo.findById(userId)
                .map(user -> {
                    // Forzamos la inicialización de la lista antes de enviarla
                    List<Producto> lista = user.getWishlist();
                    lista.size();
                    return ResponseEntity.ok(lista);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{userId}/add/{productId}")
    @Transactional // Asegura que se guarde correctamente en la tabla intermedia
    public ResponseEntity<?> addToWishlist(@PathVariable Long userId, @PathVariable Long productId) {
        Usuario user = usuarioRepo.findById(userId).orElse(null);
        Producto product = productoRepo.findById(productId).orElse(null);

        if (user == null || product == null) {
            return ResponseEntity.notFound().build();
        }

        user.addToWishlist(product);
        usuarioRepo.save(user);

        // Devolvemos un JSON para que el JS lo procese feliz
        return ResponseEntity.ok(Collections.singletonMap("message", "Producto agregado a la lista de deseos"));
    }

    @DeleteMapping("/{userId}/remove/{productId}")
    @Transactional
    public ResponseEntity<?> removeFromWishlist(@PathVariable Long userId, @PathVariable Long productId) {
        Usuario user = usuarioRepo.findById(userId).orElse(null);
        Producto product = productoRepo.findById(productId).orElse(null);

        if (user == null || product == null) {
            return ResponseEntity.notFound().build();
        }

        user.removeFromWishlist(product);
        usuarioRepo.save(user);

        return ResponseEntity.ok(Collections.singletonMap("message", "Producto eliminado de la lista de deseos"));
    }
}