package com.shareyourtrip.microservice.users.ShareYourTripUsersMs.controllers;

import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.LoginRequestDto;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.LoginResponseDto;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public LoginResponseDto login(@Valid @RequestBody LoginRequestDto request) {
        return authService.login(request);
    }
}