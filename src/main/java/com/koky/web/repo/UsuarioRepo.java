package com.koky.web.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.koky.web.domain.Usuario;
import java.util.Optional;

public interface UsuarioRepo extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    // --- NUEVO: Buscar usuario por el token de recuperación ---
    Optional<Usuario> findByResetToken(String resetToken);
}