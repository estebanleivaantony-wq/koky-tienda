package com.koky.web.service;

import com.koky.web.domain.Producto;
import com.koky.web.repo.ProductoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepo productoRepo;

    public List<Producto> getAllProductos() {
        return productoRepo.findAll();
    }

    // Método actualizado: Filtrar por categoría
    public List<Producto> getProductosByCategory(String category) {
        return productoRepo.findByCategory(category);
    }

    public Producto getProductoById(Long id) {
        return productoRepo.findById(id).orElse(null);
    }

    public List<Producto> searchProductos(String term) {
        return productoRepo.findByNameContainingIgnoreCase(term);
    }

    @Transactional
    public Producto saveProducto(Producto producto) {
        return productoRepo.save(producto);
    }

    @Transactional
    public void deleteProducto(Long id) {
        productoRepo.deleteById(id);
    }
}