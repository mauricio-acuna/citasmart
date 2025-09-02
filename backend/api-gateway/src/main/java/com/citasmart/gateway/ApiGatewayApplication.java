package com.citasmart.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * CitaSmart API Gateway Application
 * 
 * Este es el punto de entrada principal para todos los microservicios de CitaSmart.
 * Proporciona enrutamiento, autenticación, rate limiting y agregación de APIs.
 * 
 * @author CitaSmart Development Team
 * @version 1.0.0
 * @since 2025-09-01
 */
@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    /**
     * Configuración de rutas para los microservicios
     * 
     * @param builder RouteLocatorBuilder para construir las rutas
     * @return RouteLocator configurado con todas las rutas
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            // User Service Routes
            .route("user-service", r -> r.path("/api/v1/users/**")
                .uri("http://localhost:8081"))
            
            // Authentication Routes
            .route("auth-service", r -> r.path("/api/v1/auth/**")
                .uri("http://localhost:8081"))
            
            // Booking Service Routes
            .route("booking-service", r -> r.path("/api/v1/bookings/**")
                .uri("http://localhost:8082"))
            
            // Service Management Routes
            .route("service-management", r -> r.path("/api/v1/services/**")
                .uri("http://localhost:8082"))
            
            // Payment Service Routes
            .route("payment-service", r -> r.path("/api/v1/payments/**")
                .uri("http://localhost:8083"))
            
            // Professional Service Routes
            .route("professional-service", r -> r.path("/api/v1/professionals/**")
                .uri("http://localhost:8084"))
            
            // Facility Service Routes
            .route("facility-service", r -> r.path("/api/v1/facilities/**")
                .uri("http://localhost:8085"))
            
            // Notification Service Routes
            .route("notification-service", r -> r.path("/api/v1/notifications/**")
                .uri("http://localhost:8086"))
            
            // Report Service Routes
            .route("report-service", r -> r.path("/api/v1/reports/**")
                .uri("http://localhost:8087"))
            
            // Health Check Route
            .route("health-check", r -> r.path("/health/**")
                .uri("http://localhost:8080"))
            
            build();
    }

    /**
     * Configuración CORS para permitir requests desde el frontend
     * 
     * @return CorsWebFilter configurado para development y production
     */
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowCredentials(true);
        corsConfig.addAllowedOriginPattern("*");
        corsConfig.addAllowedHeader("*");
        corsConfig.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}
