package com.koky.web.security.filter;

import io.jsonwebtoken.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.koky.web.security.util.JwtUtil;
import com.koky.web.service.UsuarioDetailsService;

import org.slf4j.Logger; // Importar Logger
import org.slf4j.LoggerFactory; // Importar LoggerFactory

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class); // Inicializar Logger

    private final JwtUtil jwtUtil;
    private final UsuarioDetailsService uds;

    public JwtAuthenticationFilter(JwtUtil j, UsuarioDetailsService u) {
        this.jwtUtil = j;
        this.uds = u;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            logger.warn("No se encontró el encabezado de autorización o no comienza con Bearer.");
            chain.doFilter(request, response); // Continuar sin autenticación
            return;
        }

        String token = header.substring(7); // Extraer el token sin "Bearer "

        try {
            Jws<Claims> jws = jwtUtil.validate(token); // Valida el token
            String username = jws.getBody().getSubject(); // Obtiene el username del token

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails ud = uds.loadUserByUsername(username); // Carga los detalles del usuario
                // Crea el token de autenticación de Spring Security
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        ud, null, ud.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // Establece la autenticación en el SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.info("Usuario autenticado: {}", username);
            }
        } catch (SignatureException ex) {
            logger.error("Token JWT inválido: {}", ex.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token JWT inválido.");
            return;
        } catch (MalformedJwtException ex) {
            logger.error("Token JWT malformado: {}", ex.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token JWT malformado.");
            return;
        } catch (ExpiredJwtException ex) {
            logger.error("Token JWT expirado: {}", ex.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token JWT expirado.");
            return;
        } catch (UnsupportedJwtException ex) {
            logger.error("Token JWT no soportado: {}", ex.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token JWT no soportado.");
            return;
        } catch (IllegalArgumentException ex) {
            logger.error("El JWT compactado está vacío, null o solo espacios en blanco: {}", ex.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token JWT inválido.");
            return;
        } catch (Exception ex) {
            // Captura cualquier otra excepción inesperada
            logger.error("No se pudo establecer la autenticación del usuario: {}", ex.getMessage(), ex);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error de autenticación: " + ex.getMessage());
            return;
        }

        chain.doFilter(request, response); // Continuar con el siguiente filtro en la cadena
    }
}