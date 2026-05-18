package com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserRequestDto {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    private String phone;
    private String profilePhotoUrl;
    private String bio;
    private String language;
    private LocalDate birthDate;
    private String password;
}
