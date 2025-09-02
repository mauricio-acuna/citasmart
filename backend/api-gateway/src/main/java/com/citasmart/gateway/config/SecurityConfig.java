package com.citasmart.gateway.config;

import com.citasmart.gateway.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

/**
 * Configuración de seguridad para el API Gateway
 * 
 * Configura la autenticación JWT, autorización y políticas de seguridad
 * para todas las rutas que pasan por el gateway.
 * 
 * @author CitaSmart Development Team
 * @version 1.0.0
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Configura la cadena de filtros de seguridad
     * 
     * @param http ServerHttpSecurity para configurar
     * @return SecurityWebFilterChain configurada
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(csrf -> csrf.disable())
            .httpBasic(httpBasic -> httpBasic.disable())
            .formLogin(formLogin -> formLogin.disable())
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            .authorizeExchange(exchanges -> exchanges
                // Public endpoints - no authentication required
                .pathMatchers(
                    "/api/v1/auth/login",
                    "/api/v1/auth/register",
                    "/api/v1/auth/forgot-password",
                    "/api/v1/auth/reset-password",
                    "/api/v1/services/public/**",
                    "/health/**",
                    "/actuator/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/webjars/**"
                ).permitAll()
                
                // Admin only endpoints
                .pathMatchers(
                    "/api/v1/users/admin/**",
                    "/api/v1/reports/admin/**",
                    "/api/v1/facilities/admin/**"
                ).hasRole("ADMIN")
                
                // Professional and Admin endpoints
                .pathMatchers(
                    "/api/v1/professionals/**",
                    "/api/v1/bookings/professional/**"
                ).hasAnyRole("PROFESSIONAL", "ADMIN")
                
                // Receptionist, Professional and Admin endpoints
                .pathMatchers(
                    "/api/v1/bookings/manage/**",
                    "/api/v1/users/manage/**"
                ).hasAnyRole("RECEPTIONIST", "PROFESSIONAL", "ADMIN")
                
                // Client and above endpoints
                .pathMatchers(
                    "/api/v1/bookings/client/**",
                    "/api/v1/users/profile/**",
                    "/api/v1/payments/client/**"
                ).hasAnyRole("CLIENT", "RECEPTIONIST", "PROFESSIONAL", "ADMIN")
                
                // All other endpoints require authentication
                .anyExchange().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .build();
    }
}
