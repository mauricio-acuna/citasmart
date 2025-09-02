package com.citasmart.userservice.dto;

import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for user login
 * 
 * @author CitaSmart Team
 * @version 1.0
 */
@Schema(description = "User login request")
public class UserLoginDto {

    @NotBlank(message = "Username or email is required")
    @Schema(description = "Username or email address", example = "john_doe", required = true)
    private String usernameOrEmail;

    @NotBlank(message = "Password is required")
    @Schema(description = "User password", example = "SecurePass123", required = true)
    private String password;

    @Schema(description = "Remember me option", example = "true")
    private Boolean rememberMe = false;

    // Constructors
    public UserLoginDto() {}

    public UserLoginDto(String usernameOrEmail, String password, Boolean rememberMe) {
        this.usernameOrEmail = usernameOrEmail;
        this.password = password;
        this.rememberMe = rememberMe;
    }

    // Getters and Setters
    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }

    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(Boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    @Override
    public String toString() {
        return "UserLoginDto{" +
                "usernameOrEmail='" + usernameOrEmail + '\'' +
                ", rememberMe=" + rememberMe +
                '}';
    }
}
