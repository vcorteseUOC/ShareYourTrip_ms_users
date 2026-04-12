package com.shareyourtrip.microservice.users.ShareYourTripUsersMs.mappers;

import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.UserResponseDto;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.entitites.Role;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.entitites.User;

import java.util.Set;
import java.util.stream.Collectors;

public class UsersMapper {
    public static UserResponseDto toDto(User user) {
        Set<String> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return new UserResponseDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getProfilePhotoUrl(),
                user.getBio(),
                user.getIsActive(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getLanguage(),
                user.getBirthDate(),
                roles
        );
    }
}
