package com.example.pizzumburgum.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    @Value("${api.key.dgi:DGI-SECRET-KEY-2025}")
    private String dgiApiKey;

    @Value("${api.key.bps:BPS-SECRET-KEY-2025}")
    private String bpsApiKey;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();

        // Solo aplicar filtro a rutas /api/external/*
        if (path.startsWith("/api/external/")) {
            String apiKey = request.getHeader("X-API-Key");

            if (apiKey == null || apiKey.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"API Key requerida\"}");
                return;
            }

            // Validar API Key según el path
            boolean valid = false;
            if (path.startsWith("/api/external/dgi/")) {
                valid = dgiApiKey.equals(apiKey);
            } else if (path.startsWith("/api/external/bps/")) {
                valid = bpsApiKey.equals(apiKey);
            }

            if (!valid) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"API Key inválida\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
