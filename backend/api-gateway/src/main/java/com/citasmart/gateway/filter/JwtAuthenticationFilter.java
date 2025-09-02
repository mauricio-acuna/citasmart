package com.citasmart.gateway.filter;

import com.citasmart.gateway.service.JwtService;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Filtro de autenticación JWT para validar tokens en requests
 * 
 * Este filtro intercepta todas las requests y valida el token JWT
 * en el header Authorization. Si es válido, establece el contexto
 * de seguridad con la información del usuario.
 * 
 * @author CitaSmart Development Team
 * @version 1.0.0
 */
@Component
public class JwtAuthenticationFilter implements WebFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTHORIZATION_HEADER = HttpHeaders.AUTHORIZATION;

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * Filtra las requests para validar JWT tokens
     * 
     * @param exchange ServerWebExchange actual
     * @param chain WebFilterChain para continuar el procesamiento
     * @return Mono<Void> para el procesamiento reactivo
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String token = extractTokenFromRequest(exchange);
        
        if (token != null && jwtService.isTokenValid(token)) {
            return createAuthenticationFromToken(token)
                .flatMap(authentication -> 
                    chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication))
                );
        }
        
        return chain.filter(exchange);
    }

    /**
     * Extrae el token JWT del header Authorization
     * 
     * @param exchange ServerWebExchange actual
     * @return Token JWT sin el prefijo "Bearer " o null si no existe
     */
    private String extractTokenFromRequest(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(AUTHORIZATION_HEADER);
        
        if (StringUtils.hasText(authHeader) && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length());
        }
        
        return null;
    }

    /**
     * Crea un objeto Authentication a partir del token JWT
     * 
     * @param token Token JWT válido
     * @return Mono<Authentication> con la información del usuario
     */
    private Mono<Authentication> createAuthenticationFromToken(String token) {
        return Mono.fromCallable(() -> {
            String username = jwtService.extractUsername(token);
            List<String> roles = jwtService.extractRoles(token);
            
            List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
            
            return new UsernamePasswordAuthenticationToken(
                username, 
                null, 
                authorities
            );
        });
    }
}
