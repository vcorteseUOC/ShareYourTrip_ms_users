package com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class UserResponseDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String profilePhotoUrl;
    private String bio;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Character language;
    private LocalDate birthDate;
    private Set<String> roles;

    public UserResponseDto() {
    }

    public UserResponseDto(Long id, String firstName, String lastName, String email,
                           String phone, String profilePhotoUrl, String bio,
                           Boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt,
                           Character language, LocalDate birthDate, Set<String> roles) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.profilePhotoUrl = profilePhotoUrl;
        this.bio = bio;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.language = language;
        this.birthDate = birthDate;
        this.roles = roles;
    }

}
