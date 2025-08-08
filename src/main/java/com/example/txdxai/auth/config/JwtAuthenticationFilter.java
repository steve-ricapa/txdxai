package com.example.txdxai.auth.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Rutas públicas: auth y webhook Stripe
        if (path.startsWith("/auth")
                || path.equals("/api/stripe/webhook")
                || path.equals("/api/stripe/ping")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Falta o formato incorrecto del token");
            return;
        }

        String token = authHeader.substring(7);
        String userEmail = jwtService.extractUsername(token);

        if (StringUtils.hasText(userEmail)) {
            try {
                jwtService.validateToken(token);
            } catch (RuntimeException e) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Token inválido o expirado");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
