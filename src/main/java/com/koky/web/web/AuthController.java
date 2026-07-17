package com.koky.web.web;

import com.koky.web.domain.Rol;
import com.koky.web.domain.Usuario;
import com.koky.web.dto.LoginRequest;
import com.koky.web.dto.RegisterRequest;
import com.koky.web.repo.UsuarioRepo;
import com.koky.web.security.util.JwtUtil;
import com.koky.web.service.AuthService;
import com.koky.web.service.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; // <--- IMPORTANTE: Necesario para leer application.yml
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UsuarioRepo usuarioRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUtil jwtUtil;

    // --- INYECCIÓN DE LA URL PÚBLICA (Configurada en application.yml) ---
    // Si no encuentra la variable, usa "http://localhost:3001" por defecto para no
    // romper nada
    @Value("${app.frontend.url:http://localhost:3001}")
    private String frontendUrl;

    // ==========================================
    // 1. REGISTRO
    // ==========================================
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            Usuario newUser = authService.register(registerRequest);
            return ResponseEntity.ok(newUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==========================================
    // 2. LOGIN (MEJORADO: INCLUYE ROL)
    // ==========================================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Usuario user = authService.authenticate(request);

            // Generar Token JWT usando los roles
            List<String> roles = user.getRoles().stream()
                    .map(Rol::getName)
                    .collect(Collectors.toList());

            String token = jwtUtil.generateToken(user.getEmail(), roles);

            // --- USO DE HASHMAP PARA EVITAR ERRORES CON NULOS Y AGREGAR DATOS ---
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("name", user.getName());

            // Manejo seguro del apellido
            response.put("lastname", user.getLastName());

            response.put("email", user.getEmail());
            response.put("points", user.getPoints());
            response.put("token", token); // Token para peticiones futuras

            // *** MEJORA: ENVIAR EL ROL AL FRONTEND ***
            if (!user.getRoles().isEmpty()) {
                response.put("role", user.getRoles().get(0).getName());
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(401).body(Collections.singletonMap("error", "Credenciales inválidas"));
        }
    }

    // ==========================================
    // 3. OLVIDÉ MI CONTRASEÑA (CORREGIDO PARA URL PÚBLICA)
    // ==========================================
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        Usuario user = usuarioRepo.findByEmail(email).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "El correo no está registrado."));
        }

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        usuarioRepo.save(user);

        // *** CAMBIO CRÍTICO: USAR LA VARIABLE 'frontendUrl' ***
        // Esto generará algo como: http://72.61.9.249:3001/restablecer.html?token=...
        String link = frontendUrl + "/restablecer.html?token=" + token;

        String cuerpo = "Hola " + user.getName() + ",\n\n" +
                "Recibimos una solicitud para restablecer tu contraseña en KOKY.\n" +
                "Haz clic en el siguiente enlace para crear una nueva contraseña:\n\n" +
                link + "\n\n" +
                "Si no solicitaste esto, ignora este mensaje.";

        try {
            emailService.sendEmail(user.getEmail(), "Restablecer Contraseña - KOKY", cuerpo);
            return ResponseEntity
                    .ok(Collections.singletonMap("message", "Correo enviado. Revisa tu bandeja de entrada."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Collections.singletonMap("error", "Error al enviar el correo."));
        }
    }

    // ==========================================
    // 4. RESTABLECER CONTRASEÑA
    // ==========================================
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");

        Usuario user = usuarioRepo.findByResetToken(token).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("error", "El enlace es inválido o ha expirado."));
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        usuarioRepo.save(user);

        return ResponseEntity
                .ok(Collections.singletonMap("message", "¡Contraseña actualizada! Ya puedes iniciar sesión."));
    }
}