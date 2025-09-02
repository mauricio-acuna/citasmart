package com.citasmart.userservice.mapper;

import com.citasmart.userservice.dto.UserRegistrationDto;
import com.citasmart.userservice.dto.UserResponseDto;
import com.citasmart.userservice.dto.UserUpdateDto;
import com.citasmart.userservice.model.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-02T16:46:45+0200",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.50.v20250729-0351, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserResponseDto toUserResponseDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserResponseDto userResponseDto = new UserResponseDto();

        userResponseDto.setRoles( rolesToStrings( user.getRoles() ) );
        userResponseDto.setId( user.getId() );
        userResponseDto.setUsername( user.getUsername() );
        userResponseDto.setEmail( user.getEmail() );
        userResponseDto.setFirstName( user.getFirstName() );
        userResponseDto.setLastName( user.getLastName() );
        userResponseDto.setPhoneNumber( user.getPhoneNumber() );
        userResponseDto.setProfileImageUrl( user.getProfileImageUrl() );
        userResponseDto.setIsActive( user.getIsActive() );
        userResponseDto.setIsEmailVerified( user.getIsEmailVerified() );
        userResponseDto.setLastLoginAt( user.getLastLoginAt() );
        userResponseDto.setCreatedAt( user.getCreatedAt() );
        userResponseDto.setUpdatedAt( user.getUpdatedAt() );

        userResponseDto.setFullName( user.getFirstName() + " " + user.getLastName() );

        return userResponseDto;
    }

    @Override
    public User toUser(UserRegistrationDto registrationDto) {
        if ( registrationDto == null ) {
            return null;
        }

        User user = new User();

        user.setUsername( registrationDto.getUsername() );
        user.setEmail( registrationDto.getEmail() );
        user.setFirstName( registrationDto.getFirstName() );
        user.setLastName( registrationDto.getLastName() );
        user.setPhoneNumber( registrationDto.getPhoneNumber() );

        user.setIsActive( true );
        user.setIsEmailVerified( false );

        return user;
    }

    @Override
    public void updateUserFromDto(UserUpdateDto updateDto, User user) {
        if ( updateDto == null ) {
            return;
        }

        if ( updateDto.getFirstName() != null ) {
            user.setFirstName( updateDto.getFirstName() );
        }
        if ( updateDto.getLastName() != null ) {
            user.setLastName( updateDto.getLastName() );
        }
        if ( updateDto.getPhoneNumber() != null ) {
            user.setPhoneNumber( updateDto.getPhoneNumber() );
        }
        if ( updateDto.getProfileImageUrl() != null ) {
            user.setProfileImageUrl( updateDto.getProfileImageUrl() );
        }
    }
}
