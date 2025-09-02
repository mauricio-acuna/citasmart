package com.citasmart.userservice.service;

import com.citasmart.userservice.dto.*;
import com.citasmart.userservice.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service interface for User operations
 * 
 * @author CitaSmart Team
 * @version 1.0
 */
public interface UserService {

    /**
     * Register a new user
     */
    UserResponseDto registerUser(UserRegistrationDto registrationDto);

    /**
     * Authenticate user and generate JWT token
     */
    AuthResponseDto authenticateUser(UserLoginDto loginDto);

    /**
     * Find user by username or email
     */
    Optional<User> findByUsernameOrEmail(String usernameOrEmail);

    /**
     * Find user by ID
     */
    Optional<UserResponseDto> findById(Long id);

    /**
     * Update user profile
     */
    UserResponseDto updateProfile(Long userId, UserUpdateDto updateDto);

    /**
     * Change user password
     */
    void changePassword(Long userId, String currentPassword, String newPassword);

    /**
     * Deactivate user account
     */
    void deactivateUser(Long userId);

    /**
     * Activate user account
     */
    void activateUser(Long userId);

    /**
     * Send email verification
     */
    void sendEmailVerification(String email);

    /**
     * Verify email with token
     */
    void verifyEmail(String token);

    /**
     * Request password reset
     */
    void requestPasswordReset(String email);

    /**
     * Reset password with token
     */
    void resetPassword(String token, String newPassword);

    /**
     * Get all users with pagination
     */
    Page<UserResponseDto> getAllUsers(Pageable pageable);

    /**
     * Search users
     */
    Page<UserResponseDto> searchUsers(String searchTerm, Pageable pageable);

    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Update last login timestamp
     */
    void updateLastLogin(Long userId);

    /**
     * Assign role to user
     */
    void assignRole(Long userId, String roleName);

    /**
     * Remove role from user
     */
    void removeRole(Long userId, String roleName);

    /**
     * Get user statistics
     */
    UserStatsDto getUserStats();
}
