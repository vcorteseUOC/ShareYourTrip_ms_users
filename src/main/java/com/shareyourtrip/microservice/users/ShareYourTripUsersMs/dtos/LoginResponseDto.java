package com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Set<String> roles;
    private String message;
    private String token;
}
