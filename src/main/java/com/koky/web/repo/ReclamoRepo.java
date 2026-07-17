package com.koky.web.repo;

import com.koky.web.domain.Reclamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List; // Importar List

@Repository
public interface ReclamoRepo extends JpaRepository<Reclamo, Long> {
    // Nuevo método para buscar historial
    List<Reclamo> findByUserIdOrderByFechaDesc(Long userId);
}