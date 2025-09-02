package com.citasmart.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for authentication response
 * 
 * @author CitaSmart Team
 * @version 1.0
 */
@Schema(description = "Authentication response with JWT token")
public class AuthResponseDto {

    @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "Token type", example = "Bearer")
    private String tokenType = "Bearer";

    @Schema(description = "Token expiration in seconds", example = "3600")
    private Long expiresIn;

    @Schema(description = "User information")
    private UserResponseDto user;

    // Constructors
    public AuthResponseDto() {}

    public AuthResponseDto(String accessToken, Long expiresIn, UserResponseDto user) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.user = user;
    }

    // Getters and Setters
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public UserResponseDto getUser() {
        return user;
    }

    public void setUser(UserResponseDto user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "AuthResponseDto{" +
                "tokenType='" + tokenType + '\'' +
                ", expiresIn=" + expiresIn +
                ", user=" + user +
                '}';
    }
}
