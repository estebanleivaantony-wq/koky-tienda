package com.koky.web.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.koky.web.domain.Producto;
import com.koky.web.service.ProductoService;

import java.util.List;

@RestController
@RequestMapping("/api/products") // <<< CAMBIO: Ahora la URL base es /api/products
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    /**
     * Obtener todos los productos.
     * GET /api/products
     */
    @GetMapping
    public ResponseEntity<List<Producto>> getAllProductos() {
        List<Producto> products = productoService.getAllProductos();
        return ResponseEntity.ok(products);
    }

    /**
     * Obtener productos por categoría (Ej: Escolar, Oficina, Stitch).
     * GET /api/products/category/{category}
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Producto>> getProductosByCategory(@PathVariable String category) {
        List<Producto> products = productoService.getProductosByCategory(category);
        return ResponseEntity.ok(products);
    }

    /**
     * Buscar productos por nombre (Buscador).
     * GET /api/products/search?q=mochila
     */
    @GetMapping("/search")
    public ResponseEntity<List<Producto>> searchProductos(@RequestParam("q") String query) {
        List<Producto> products = productoService.searchProductos(query);
        return ResponseEntity.ok(products);
    }

    /**
     * Obtener un producto por ID.
     * GET /api/products/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Producto> getProductoById(@PathVariable Long id) {
        Producto product = productoService.getProductoById(id);
        if (product != null) {
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // --- ADMINISTRACIÓN (Crear, Actualizar, Borrar) ---

    /**
     * Crear un nuevo producto.
     * POST /api/products
     */
    @PostMapping
    public ResponseEntity<Producto> createProducto(@RequestBody Producto product) {
        Producto savedProduct = productoService.saveProducto(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    /**
     * Actualizar un producto.
     * PUT /api/products/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Producto> updateProducto(@PathVariable Long id, @RequestBody Producto product) {
        Producto existingProduct = productoService.getProductoById(id);
        if (existingProduct != null) {
            product.setId(id); // Asegurar que el ID sea el correcto
            Producto updatedProduct = productoService.saveProducto(product);
            return ResponseEntity.ok(updatedProduct);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Eliminar un producto.
     * DELETE /api/products/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducto(@PathVariable Long id) {
        if (productoService.getProductoById(id) != null) {
            productoService.deleteProducto(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}