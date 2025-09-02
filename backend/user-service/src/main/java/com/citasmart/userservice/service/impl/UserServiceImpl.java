package com.citasmart.userservice.service.impl;

import com.citasmart.userservice.dto.*;
import com.citasmart.userservice.model.Role;
import com.citasmart.userservice.model.User;
import com.citasmart.userservice.repository.RoleRepository;
import com.citasmart.userservice.repository.UserRepository;
import com.citasmart.userservice.service.UserService;
import com.citasmart.userservice.service.JwtService;
import com.citasmart.userservice.service.EmailService;
import com.citasmart.userservice.mapper.UserMapper;
import com.citasmart.userservice.exception.UserAlreadyExistsException;
import com.citasmart.userservice.exception.UserNotFoundException;
import com.citasmart.userservice.exception.InvalidCredentialsException;
import com.citasmart.userservice.exception.InvalidTokenException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Implementation of UserService
 * Handles all user-related business logic including authentication, registration, and profile management
 * 
 * @author CitaSmart Team
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final UserMapper userMapper;

    @Value("${app.jwt.expiration}")
    private Long jwtExpirationMs;

    @Value("${app.email.verification.enabled:true}")
    private Boolean emailVerificationEnabled;

    @Value("${app.email.verification.token.expiration}")
    private Long emailVerificationTokenExpiration;

    @Value("${app.password.reset.token.expiration}")
    private Long passwordResetTokenExpiration;

    /**
     * Register a new user in the system
     * 
     * @param registrationDto User registration data
     * @return UserResponseDto with user information
     * @throws UserAlreadyExistsException if username or email already exists
     */
    @Override
    public UserResponseDto registerUser(UserRegistrationDto registrationDto) {
        log.info("Registering new user with username: {}", registrationDto.getUsername());

        // Check if user already exists
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists: " + registrationDto.getUsername());
        }

        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists: " + registrationDto.getEmail());
        }

        // Create new user
        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setFirstName(registrationDto.getFirstName());
        user.setLastName(registrationDto.getLastName());
        user.setPhoneNumber(registrationDto.getPhoneNumber());
        user.setIsActive(true);
        user.setIsEmailVerified(!emailVerificationEnabled);

        // Assign default CLIENT role
        Role clientRole = roleRepository.findByName("CLIENT")
                .orElseThrow(() -> new RuntimeException("Default CLIENT role not found"));
        user.setRoles(Set.of(clientRole));

        // Generate email verification token if enabled
        if (emailVerificationEnabled) {
            user.setEmailVerificationToken(UUID.randomUUID().toString());
        }

        // Save user
        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        // Send verification email if enabled
        if (emailVerificationEnabled) {
            emailService.sendEmailVerification(savedUser.getEmail(), savedUser.getEmailVerificationToken());
        }

        return userMapper.toUserResponseDto(savedUser);
    }

    /**
     * Authenticate user and generate JWT token
     * 
     * @param loginDto User login credentials
     * @return AuthResponseDto with JWT token and user information
     * @throws InvalidCredentialsException if credentials are invalid
     */
    @Override
    public AuthResponseDto authenticateUser(UserLoginDto loginDto) {
        log.info("Authenticating user: {}", loginDto.getUsernameOrEmail());

        // Find user by username or email
        User user = userRepository.findByUsernameOrEmail(loginDto.getUsernameOrEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        // Check password
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        // Check if user is active
        if (!user.getIsActive()) {
            throw new InvalidCredentialsException("User account is deactivated");
        }

        // Generate JWT token
        String token = jwtService.generateToken(user.getUsername());

        // Update last login
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        // Create response
        UserResponseDto userResponse = userMapper.toUserResponseDto(user);
        AuthResponseDto authResponse = new AuthResponseDto();
        authResponse.setAccessToken(token);
        authResponse.setExpiresIn(jwtExpirationMs / 1000); // Convert to seconds
        authResponse.setUser(userResponse);

        log.info("User authenticated successfully: {}", user.getUsername());
        return authResponse;
    }

    /**
     * Find user by username or email
     */
    @Override
    @Cacheable(value = "users", key = "#usernameOrEmail")
    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail);
    }

    /**
     * Find user by ID
     */
    @Override
    @Cacheable(value = "users", key = "#id")
    public Optional<UserResponseDto> findById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toUserResponseDto);
    }

    /**
     * Update user profile
     */
    @Override
    @CacheEvict(value = "users", allEntries = true)
    public UserResponseDto updateProfile(Long userId, UserUpdateDto updateDto) {
        log.info("Updating profile for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        // Update fields if provided
        if (updateDto.getFirstName() != null) {
            user.setFirstName(updateDto.getFirstName());
        }
        if (updateDto.getLastName() != null) {
            user.setLastName(updateDto.getLastName());
        }
        if (updateDto.getPhoneNumber() != null) {
            user.setPhoneNumber(updateDto.getPhoneNumber());
        }
        if (updateDto.getProfileImageUrl() != null) {
            user.setProfileImageUrl(updateDto.getProfileImageUrl());
        }

        User updatedUser = userRepository.save(user);
        log.info("Profile updated successfully for user ID: {}", userId);

        return userMapper.toUserResponseDto(updatedUser);
    }

    /**
     * Change user password
     */
    @Override
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        log.info("Changing password for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        log.info("Password changed successfully for user ID: {}", userId);
    }

    /**
     * Deactivate user account
     */
    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void deactivateUser(Long userId) {
        log.info("Deactivating user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        user.setIsActive(false);
        userRepository.save(user);

        log.info("User deactivated successfully: {}", userId);
    }

    /**
     * Activate user account
     */
    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void activateUser(Long userId) {
        log.info("Activating user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        user.setIsActive(true);
        userRepository.save(user);

        log.info("User activated successfully: {}", userId);
    }

    /**
     * Send email verification
     */
    @Override
    public void sendEmailVerification(String email) {
        log.info("Sending email verification to: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        if (user.getIsEmailVerified()) {
            log.warn("Email already verified for user: {}", email);
            return;
        }

        // Generate new verification token
        String token = UUID.randomUUID().toString();
        user.setEmailVerificationToken(token);
        userRepository.save(user);

        // Send verification email
        emailService.sendEmailVerification(email, token);
        log.info("Email verification sent to: {}", email);
    }

    /**
     * Verify email with token
     */
    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void verifyEmail(String token) {
        log.info("Verifying email with token: {}", token);

        User user = userRepository.findByEmailVerificationToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid email verification token"));

        user.setIsEmailVerified(true);
        user.setEmailVerificationToken(null);
        userRepository.save(user);

        log.info("Email verified successfully for user: {}", user.getEmail());
    }

    /**
     * Request password reset
     */
    @Override
    public void requestPasswordReset(String email) {
        log.info("Password reset requested for email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        // Generate reset token
        String token = UUID.randomUUID().toString();
        user.setPasswordResetToken(token);
        user.setPasswordResetExpiresAt(LocalDateTime.now().plusSeconds(passwordResetTokenExpiration / 1000));
        userRepository.save(user);

        // Send reset email
        emailService.sendPasswordReset(email, token);
        log.info("Password reset email sent to: {}", email);
    }

    /**
     * Reset password with token
     */
    @Override
    public void resetPassword(String token, String newPassword) {
        log.info("Resetting password with token: {}", token);

        User user = userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid password reset token"));

        // Check if token is expired
        if (user.getPasswordResetExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Password reset token has expired");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetExpiresAt(null);
        userRepository.save(user);

        log.info("Password reset successfully for user: {}", user.getEmail());
    }

    /**
     * Get all users with pagination
     */
    @Override
    public Page<UserResponseDto> getAllUsers(Pageable pageable) {
        log.debug("Getting all users with pagination: {}", pageable);
        return userRepository.findAll(pageable)
                .map(userMapper::toUserResponseDto);
    }

    /**
     * Search users
     */
    @Override
    public Page<UserResponseDto> searchUsers(String searchTerm, Pageable pageable) {
        log.debug("Searching users with term: {}", searchTerm);
        return userRepository.searchUsers(searchTerm, pageable)
                .map(userMapper::toUserResponseDto);
    }

    /**
     * Check if username exists
     */
    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Check if email exists
     */
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Update last login timestamp
     */
    @Override
    public void updateLastLogin(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);
        });
    }

    /**
     * Assign role to user
     */
    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void assignRole(Long userId, String roleName) {
        log.info("Assigning role {} to user ID: {}", roleName, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

        user.getRoles().add(role);
        userRepository.save(user);

        log.info("Role {} assigned successfully to user ID: {}", roleName, userId);
    }

    /**
     * Remove role from user
     */
    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void removeRole(Long userId, String roleName) {
        log.info("Removing role {} from user ID: {}", roleName, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        user.getRoles().removeIf(role -> role.getName().equals(roleName));
        userRepository.save(user);

        log.info("Role {} removed successfully from user ID: {}", roleName, userId);
    }

    /**
     * Get user statistics
     */
    @Override
    @Cacheable(value = "userStats", key = "'stats'")
    public UserStatsDto getUserStats() {
        log.debug("Getting user statistics");

        Long totalUsers = userRepository.count();
        Long activeUsers = userRepository.countByIsActiveTrue();
        Long verifiedUsers = userRepository.countByIsEmailVerifiedTrue();
        
        // Calculate new users this month
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime now = LocalDateTime.now();
        Long newUsersThisMonth = userRepository.findUsersCreatedBetween(startOfMonth, now).size();

        // Count users by role
        Long adminCount = userRepository.countUsersByRoleName("ADMIN");
        Long providerCount = userRepository.countUsersByRoleName("PROVIDER");
        Long clientCount = userRepository.countUsersByRoleName("CLIENT");
        Long supportCount = userRepository.countUsersByRoleName("SUPPORT");
        Long managerCount = userRepository.countUsersByRoleName("MANAGER");

        UserStatsDto stats = new UserStatsDto();
        stats.setTotalUsers(totalUsers);
        stats.setActiveUsers(activeUsers);
        stats.setVerifiedUsers(verifiedUsers);
        stats.setNewUsersThisMonth(newUsersThisMonth);
        stats.setAdminCount(adminCount);
        stats.setProviderCount(providerCount);
        stats.setClientCount(clientCount);
        stats.setSupportCount(supportCount);
        stats.setManagerCount(managerCount);

        return stats;
    }
}
