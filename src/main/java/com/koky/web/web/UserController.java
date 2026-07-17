package com.koky.web.web;

import com.koky.web.domain.Usuario;
import com.koky.web.repo.UsuarioRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UsuarioRepo usuarioRepo;

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody Map<String, String> updates) {
        Usuario usuario = usuarioRepo.findById(id).orElse(null);

        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }

        // Actualizamos solo si envían datos
        if (updates.containsKey("name")) {
            usuario.setName(updates.get("name"));
        }
        if (updates.containsKey("lastname")) {
            usuario.setLastName(updates.get("lastname"));
        }

        usuarioRepo.save(usuario);

        // --- CORRECCIÓN AQUÍ: Usar HashMap para evitar error si el apellido es NULL
        // ---
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Usuario actualizado");
        response.put("name", usuario.getName());

        // Si el apellido es null, enviamos una cadena vacía o el null, pero sin que
        // Java explote
        response.put("lastname", usuario.getLastName() != null ? usuario.getLastName() : "");

        return ResponseEntity.ok(response);
    }
}