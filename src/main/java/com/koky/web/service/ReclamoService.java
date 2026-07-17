package com.koky.web.service;

import com.koky.web.domain.Reclamo;
import com.koky.web.repo.ReclamoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class ReclamoService {

    @Autowired
    private ReclamoRepo reclamoRepo;

    public Reclamo registrarReclamo(Reclamo reclamo) {
        // Generar un código único (Ej: REC-A1B2)
        String codigo = "REC-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        reclamo.setCodigo(codigo);
        return reclamoRepo.save(reclamo);
    }
}