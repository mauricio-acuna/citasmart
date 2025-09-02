package com.citasmart.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for user statistics
 * 
 * @author CitaSmart Team
 * @version 1.0
 */
@Schema(description = "User statistics")
public class UserStatsDto {

    @Schema(description = "Total number of users", example = "1000")
    private Long totalUsers;

    @Schema(description = "Number of active users", example = "950")
    private Long activeUsers;

    @Schema(description = "Number of verified users", example = "800")
    private Long verifiedUsers;

    @Schema(description = "Number of new users this month", example = "50")
    private Long newUsersThisMonth;

    @Schema(description = "Number of admins", example = "5")
    private Long adminCount;

    @Schema(description = "Number of providers", example = "200")
    private Long providerCount;

    @Schema(description = "Number of clients", example = "750")
    private Long clientCount;

    @Schema(description = "Number of support staff", example = "20")
    private Long supportCount;

    @Schema(description = "Number of managers", example = "10")
    private Long managerCount;

    // Constructors
    public UserStatsDto() {}

    public UserStatsDto(Long totalUsers, Long activeUsers, Long verifiedUsers, Long newUsersThisMonth,
                       Long adminCount, Long providerCount, Long clientCount, Long supportCount, Long managerCount) {
        this.totalUsers = totalUsers;
        this.activeUsers = activeUsers;
        this.verifiedUsers = verifiedUsers;
        this.newUsersThisMonth = newUsersThisMonth;
        this.adminCount = adminCount;
        this.providerCount = providerCount;
        this.clientCount = clientCount;
        this.supportCount = supportCount;
        this.managerCount = managerCount;
    }

    // Getters and Setters
    public Long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public Long getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(Long activeUsers) {
        this.activeUsers = activeUsers;
    }

    public Long getVerifiedUsers() {
        return verifiedUsers;
    }

    public void setVerifiedUsers(Long verifiedUsers) {
        this.verifiedUsers = verifiedUsers;
    }

    public Long getNewUsersThisMonth() {
        return newUsersThisMonth;
    }

    public void setNewUsersThisMonth(Long newUsersThisMonth) {
        this.newUsersThisMonth = newUsersThisMonth;
    }

    public Long getAdminCount() {
        return adminCount;
    }

    public void setAdminCount(Long adminCount) {
        this.adminCount = adminCount;
    }

    public Long getProviderCount() {
        return providerCount;
    }

    public void setProviderCount(Long providerCount) {
        this.providerCount = providerCount;
    }

    public Long getClientCount() {
        return clientCount;
    }

    public void setClientCount(Long clientCount) {
        this.clientCount = clientCount;
    }

    public Long getSupportCount() {
        return supportCount;
    }

    public void setSupportCount(Long supportCount) {
        this.supportCount = supportCount;
    }

    public Long getManagerCount() {
        return managerCount;
    }

    public void setManagerCount(Long managerCount) {
        this.managerCount = managerCount;
    }

    @Override
    public String toString() {
        return "UserStatsDto{" +
                "totalUsers=" + totalUsers +
                ", activeUsers=" + activeUsers +
                ", verifiedUsers=" + verifiedUsers +
                ", newUsersThisMonth=" + newUsersThisMonth +
                ", adminCount=" + adminCount +
                ", providerCount=" + providerCount +
                ", clientCount=" + clientCount +
                ", supportCount=" + supportCount +
                ", managerCount=" + managerCount +
                '}';
    }
}
