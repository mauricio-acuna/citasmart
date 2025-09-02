package com.citasmart.userservice.controller;

import com.citasmart.userservice.dto.*;
import com.citasmart.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for User operations
 * 
 * @author CitaSmart Team
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for user registration, authentication, and profile management")
public class UserController {

    private final UserService userService;

    /**
     * Register a new user
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Create a new user account in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User registered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "User already exists")
    })
    public ResponseEntity<UserResponseDto> registerUser(
            @Valid @RequestBody UserRegistrationDto registrationDto) {
        log.info("REST request to register user: {}", registrationDto.getUsername());
        
        UserResponseDto userResponse = userService.registerUser(registrationDto);
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    /**
     * Authenticate user
     */
    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Login with username/email and password")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Authentication successful"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<AuthResponseDto> loginUser(
            @Valid @RequestBody UserLoginDto loginDto) {
        log.info("REST request to login user: {}", loginDto.getUsernameOrEmail());
        
        AuthResponseDto authResponse = userService.authenticateUser(loginDto);
        return ResponseEntity.ok(authResponse);
    }

    /**
     * Get user profile by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userService.isCurrentUser(#id)")
    @Operation(summary = "Get user by ID", description = "Retrieve user information by user ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponseDto> getUserById(
            @Parameter(description = "User ID") @PathVariable Long id) {
        log.info("REST request to get user by ID: {}", id);
        
        return userService.findById(id)
                .map(user -> ResponseEntity.ok(user))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update user profile
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userService.isCurrentUser(#id)")
    @Operation(summary = "Update user profile", description = "Update user profile information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<UserResponseDto> updateProfile(
            @Parameter(description = "User ID") @PathVariable Long id,
            @Valid @RequestBody UserUpdateDto updateDto) {
        log.info("REST request to update profile for user ID: {}", id);
        
        UserResponseDto updatedUser = userService.updateProfile(id, updateDto);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Change password
     */
    @PostMapping("/{id}/change-password")
    @PreAuthorize("hasRole('ADMIN') or @userService.isCurrentUser(#id)")
    @Operation(summary = "Change password", description = "Change user password")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password changed successfully"),
        @ApiResponse(responseCode = "401", description = "Current password is incorrect"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Void> changePassword(
            @Parameter(description = "User ID") @PathVariable Long id,
            @Valid @RequestBody ChangePasswordDto changePasswordDto) {
        log.info("REST request to change password for user ID: {}", id);
        
        userService.changePassword(id, changePasswordDto.getCurrentPassword(), 
                                 changePasswordDto.getNewPassword());
        return ResponseEntity.ok().build();
    }

    /**
     * Get all users (Admin only)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Get all users", description = "Retrieve all users with pagination (Admin/Manager only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Page<UserResponseDto>> getAllUsers(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        log.info("REST request to get all users with pagination: {}", pageable);
        
        Page<UserResponseDto> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * Search users (Admin/Manager only)
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Search users", description = "Search users by name or email (Admin/Manager only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search completed successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Page<UserResponseDto>> searchUsers(
            @Parameter(description = "Search term") @RequestParam String searchTerm,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        log.info("REST request to search users with term: {}", searchTerm);
        
        Page<UserResponseDto> users = userService.searchUsers(searchTerm, pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * Deactivate user (Admin only)
     */
    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate user", description = "Deactivate a user account (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User deactivated successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Void> deactivateUser(
            @Parameter(description = "User ID") @PathVariable Long id) {
        log.info("REST request to deactivate user ID: {}", id);
        
        userService.deactivateUser(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Activate user (Admin only)
     */
    @PostMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activate user", description = "Activate a user account (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User activated successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Void> activateUser(
            @Parameter(description = "User ID") @PathVariable Long id) {
        log.info("REST request to activate user ID: {}", id);
        
        userService.activateUser(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Assign role to user (Admin only)
     */
    @PostMapping("/{id}/roles/{roleName}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Assign role to user", description = "Assign a role to a user (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Role assigned successfully"),
        @ApiResponse(responseCode = "404", description = "User or role not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Void> assignRole(
            @Parameter(description = "User ID") @PathVariable Long id,
            @Parameter(description = "Role name") @PathVariable String roleName) {
        log.info("REST request to assign role {} to user ID: {}", roleName, id);
        
        userService.assignRole(id, roleName);
        return ResponseEntity.ok().build();
    }

    /**
     * Remove role from user (Admin only)
     */
    @DeleteMapping("/{id}/roles/{roleName}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remove role from user", description = "Remove a role from a user (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Role removed successfully"),
        @ApiResponse(responseCode = "404", description = "User or role not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Void> removeRole(
            @Parameter(description = "User ID") @PathVariable Long id,
            @Parameter(description = "Role name") @PathVariable String roleName) {
        log.info("REST request to remove role {} from user ID: {}", roleName, id);
        
        userService.removeRole(id, roleName);
        return ResponseEntity.ok().build();
    }

    /**
     * Send email verification
     */
    @PostMapping("/send-verification")
    @Operation(summary = "Send email verification", description = "Send email verification to user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Verification email sent"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Void> sendEmailVerification(
            @Parameter(description = "Email address") @RequestParam String email) {
        log.info("REST request to send email verification to: {}", email);
        
        userService.sendEmailVerification(email);
        return ResponseEntity.ok().build();
    }

    /**
     * Verify email
     */
    @PostMapping("/verify-email")
    @Operation(summary = "Verify email", description = "Verify email address with token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email verified successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid token")
    })
    public ResponseEntity<Void> verifyEmail(
            @Parameter(description = "Verification token") @RequestParam String token) {
        log.info("REST request to verify email with token: {}", token);
        
        userService.verifyEmail(token);
        return ResponseEntity.ok().build();
    }

    /**
     * Request password reset
     */
    @PostMapping("/request-password-reset")
    @Operation(summary = "Request password reset", description = "Request password reset for user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password reset email sent"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Void> requestPasswordReset(
            @Parameter(description = "Email address") @RequestParam String email) {
        log.info("REST request for password reset for email: {}", email);
        
        userService.requestPasswordReset(email);
        return ResponseEntity.ok().build();
    }

    /**
     * Reset password
     */
    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Reset password with token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password reset successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid or expired token")
    })
    public ResponseEntity<Void> resetPassword(
            @Valid @RequestBody ResetPasswordDto resetPasswordDto) {
        log.info("REST request to reset password with token: {}", resetPasswordDto.getToken());
        
        userService.resetPassword(resetPasswordDto.getToken(), resetPasswordDto.getNewPassword());
        return ResponseEntity.ok().build();
    }

    /**
     * Check username availability
     */
    @GetMapping("/check-username")
    @Operation(summary = "Check username availability", description = "Check if username is available")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Username availability checked")
    })
    public ResponseEntity<Boolean> checkUsernameAvailability(
            @Parameter(description = "Username") @RequestParam String username) {
        log.info("REST request to check username availability: {}", username);
        
        boolean exists = userService.existsByUsername(username);
        return ResponseEntity.ok(!exists); // Return true if available (not exists)
    }

    /**
     * Check email availability
     */
    @GetMapping("/check-email")
    @Operation(summary = "Check email availability", description = "Check if email is available")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email availability checked")
    })
    public ResponseEntity<Boolean> checkEmailAvailability(
            @Parameter(description = "Email") @RequestParam String email) {
        log.info("REST request to check email availability: {}", email);
        
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(!exists); // Return true if available (not exists)
    }

    /**
     * Get user statistics (Admin/Manager only)
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Get user statistics", description = "Get user statistics (Admin/Manager only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<UserStatsDto> getUserStats() {
        log.info("REST request to get user statistics");
        
        UserStatsDto stats = userService.getUserStats();
        return ResponseEntity.ok(stats);
    }
}
