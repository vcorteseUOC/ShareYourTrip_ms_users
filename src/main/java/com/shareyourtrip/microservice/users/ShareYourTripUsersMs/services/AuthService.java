package com.shareyourtrip.microservice.users.ShareYourTripUsersMs.services;

import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.LoginRequestDto;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.LoginResponseDto;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.RegisterRequestDto;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.UserResponseDto;

public interface AuthService {
    LoginResponseDto login(LoginRequestDto request);
    UserResponseDto register(RegisterRequestDto request);
}
