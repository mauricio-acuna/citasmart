package com.citasmart.userservice.mapper;

import com.citasmart.userservice.dto.UserRegistrationDto;
import com.citasmart.userservice.dto.UserResponseDto;
import com.citasmart.userservice.dto.UserUpdateDto;
import com.citasmart.userservice.model.Role;
import com.citasmart.userservice.model.User;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * MapStruct mapper for User entity and DTOs
 * 
 * @author CitaSmart Team
 * @version 1.0
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper {

    /**
     * Convert User entity to UserResponseDto
     * 
     * @param user User entity
     * @return UserResponseDto
     */
    @Mapping(target = "fullName", expression = "java(user.getFirstName() + \" \" + user.getLastName())")
    @Mapping(target = "roles", source = "roles", qualifiedByName = "rolesToStrings")
    UserResponseDto toUserResponseDto(User user);

    /**
     * Convert UserRegistrationDto to User entity
     * 
     * @param registrationDto UserRegistrationDto
     * @return User entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true) // Will be encoded separately
    @Mapping(target = "roles", ignore = true) // Will be set separately
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "isEmailVerified", constant = "false")
    @Mapping(target = "emailVerificationToken", ignore = true)
    @Mapping(target = "passwordResetToken", ignore = true)
    @Mapping(target = "passwordResetExpiresAt", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toUser(UserRegistrationDto registrationDto);

    /**
     * Update User entity from UserUpdateDto
     * 
     * @param updateDto UserUpdateDto
     * @param user User entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "isEmailVerified", ignore = true)
    @Mapping(target = "emailVerificationToken", ignore = true)
    @Mapping(target = "passwordResetToken", ignore = true)
    @Mapping(target = "passwordResetExpiresAt", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateUserFromDto(UserUpdateDto updateDto, @MappingTarget User user);

    /**
     * Convert Set of Role entities to Set of role names
     * 
     * @param roles Set of Role entities
     * @return Set of role names
     */
    @Named("rolesToStrings")
    default Set<String> rolesToStrings(Set<Role> roles) {
        if (roles == null) {
            return null;
        }
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }

    /**
     * Convert role name to Role entity (partial mapping)
     * 
     * @param roleName Role name
     * @return Role entity with name set
     */
    @Named("stringToRole")
    default Role stringToRole(String roleName) {
        if (roleName == null) {
            return null;
        }
        Role role = new Role();
        role.setName(roleName);
        return role;
    }
}
