package com.shareyourtrip.microservice.users.ShareYourTripUsersMs.services;

import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.LoginRequestDto;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.LoginResponseDto;

public interface AuthService {
    LoginResponseDto login(LoginRequestDto request);
}
