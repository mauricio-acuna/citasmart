package com.citasmart.userservice.dto;

import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for user profile update
 * 
 * @author CitaSmart Team
 * @version 1.0
 */
@Schema(description = "User profile update request")
public class UserUpdateDto {

    @Size(max = 50, message = "First name must not exceed 50 characters")
    @Schema(description = "User first name", example = "John")
    private String firstName;

    @Size(max = 50, message = "Last name must not exceed 50 characters")
    @Schema(description = "User last name", example = "Doe")
    private String lastName;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number format is invalid")
    @Schema(description = "User phone number", example = "+1234567890")
    private String phoneNumber;

    @Schema(description = "Profile image URL")
    private String profileImageUrl;

    // Constructors
    public UserUpdateDto() {}

    public UserUpdateDto(String firstName, String lastName, String phoneNumber, String profileImageUrl) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.profileImageUrl = profileImageUrl;
    }

    // Getters and Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    @Override
    public String toString() {
        return "UserUpdateDto{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", profileImageUrl='" + profileImageUrl + '\'' +
                '}';
    }
}
