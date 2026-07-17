package com.koky.web.web;

import com.koky.web.domain.Reclamo;
import com.koky.web.repo.ReclamoRepo; // Usamos el repo directamente para simplificar
import com.koky.web.service.ReclamoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reclamos")
public class ReclamoController {

    @Autowired
    private ReclamoService reclamoService;

    @Autowired
    private ReclamoRepo reclamoRepo; // Inyección del repo

    @PostMapping("/crear")
    public ResponseEntity<?> crearReclamo(@RequestBody Reclamo reclamo) {
        try {
            Reclamo nuevo = reclamoService.registrarReclamo(reclamo);
            return ResponseEntity.ok(Map.of("mensaje", "Reclamo registrado", "codigo", nuevo.getCodigo()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Error al registrar"));
        }
    }

    // --- NUEVO ENDPOINT PARA HISTORIAL ---
    @GetMapping("/usuario/{userId}")
    public ResponseEntity<List<Reclamo>> listarPorUsuario(@PathVariable Long userId) {
        List<Reclamo> lista = reclamoRepo.findByUserIdOrderByFechaDesc(userId);
        return ResponseEntity.ok(lista);
    }
}