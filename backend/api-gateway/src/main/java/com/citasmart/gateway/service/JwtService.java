package com.citasmart.gateway.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

/**
 * Servicio para manejo de JWT tokens
 * 
 * Proporciona funcionalidades para validar, parsear y extraer información
 * de tokens JWT utilizados para autenticación en el sistema.
 * 
 * @author CitaSmart Development Team
 * @version 1.0.0
 */
@Service
public class JwtService {

    @Value("${citasmart.jwt.secret:dGhpc0lzQVZlcnlTZWNyZXRLZXlGb3JDaXRhU21hcnRBcHBsaWNhdGlvbjIwMjU=}")
    private String secretKey;

    @Value("${citasmart.jwt.expiration:86400000}")
    private long jwtExpiration;

    /**
     * Extrae el nombre de usuario del token JWT
     * 
     * @param token Token JWT
     * @return Nombre de usuario
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrae los roles del usuario del token JWT
     * 
     * @param token Token JWT
     * @return Lista de roles del usuario
     */
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return extractClaim(token, claims -> (List<String>) claims.get("roles"));
    }

    /**
     * Extrae el ID del usuario del token JWT
     * 
     * @param token Token JWT
     * @return ID del usuario
     */
    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Long.class));
    }

    /**
     * Extrae una claim específica del token JWT
     * 
     * @param token Token JWT
     * @param claimsResolver Función para extraer la claim deseada
     * @param <T> Tipo de la claim
     * @return Valor de la claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Valida si el token JWT es válido
     * 
     * @param token Token JWT a validar
     * @return true si el token es válido, false en caso contrario
     */
    public boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verifica si el token JWT ha expirado
     * 
     * @param token Token JWT
     * @return true si el token ha expirado, false en caso contrario
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extrae la fecha de expiración del token JWT
     * 
     * @param token Token JWT
     * @return Fecha de expiración
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrae todas las claims del token JWT
     * 
     * @param token Token JWT
     * @return Claims del token
     */
    private Claims extractAllClaims(String token) {
        return Jwts
            .parser()
            .verifyWith(getSignInKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    /**
     * Obtiene la clave de firma para validar tokens JWT
     * 
     * @return Clave secreta para firma
     */
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
