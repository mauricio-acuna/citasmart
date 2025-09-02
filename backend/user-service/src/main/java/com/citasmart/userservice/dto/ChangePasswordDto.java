package com.citasmart.userservice.dto;

import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for password change request
 * 
 * @author CitaSmart Team
 * @version 1.0
 */
@Schema(description = "Password change request")
public class ChangePasswordDto {

    @NotBlank(message = "Current password is required")
    @Schema(description = "Current password", example = "currentPass123", required = true)
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "New password must be at least 8 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", 
             message = "New password must contain at least one lowercase letter, one uppercase letter, and one digit")
    @Schema(description = "New password", example = "newSecurePass123", required = true)
    private String newPassword;

    @NotBlank(message = "Password confirmation is required")
    @Schema(description = "New password confirmation", example = "newSecurePass123", required = true)
    private String confirmPassword;

    // Constructors
    public ChangePasswordDto() {}

    public ChangePasswordDto(String currentPassword, String newPassword, String confirmPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    // Getters and Setters
    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    // Custom validation
    @AssertTrue(message = "New password and confirmation password must match")
    public boolean isPasswordsMatching() {
        return newPassword != null && newPassword.equals(confirmPassword);
    }

    @Override
    public String toString() {
        return "ChangePasswordDto{" +
                "currentPassword='[PROTECTED]', " +
                "newPassword='[PROTECTED]', " +
                "confirmPassword='[PROTECTED]'" +
                '}';
    }
}
