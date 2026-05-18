package com.shareyourtrip.microservice.users.ShareYourTripUsersMs.mappers;

import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.RegisterRequestDto;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.entitites.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AuthMapper {
    public static User toEntity(RegisterRequestDto request, PasswordEncoder passwordEncoder) {
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setBirthDate(request.getBirthDate() != null ? LocalDate.parse(request.getBirthDate()) : null);
        user.setLanguage(request.getLanguage().toUpperCase());
        user.setBio(request.getBio());
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }
}
