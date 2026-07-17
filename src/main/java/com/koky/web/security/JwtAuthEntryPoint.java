package com.koky.web.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        // Esto se ejecutará cuando un usuario no autenticado intente acceder a un
        // recurso protegido.
        // O cuando se envíe un token JWT inválido (si el filtro JWT arroja una
        // excepción).
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                "Acceso denegado: Necesitas un token de autenticación válido.");
    }
}