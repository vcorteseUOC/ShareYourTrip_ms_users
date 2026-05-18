package com.shareyourtrip.microservice.users.ShareYourTripUsersMs.services;

import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.UserRequestDto;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.UserResponseDto;

import java.util.List;

public interface UserService {

    List<UserResponseDto> getAllUsers();

    UserResponseDto getUserById(Long id);

    UserResponseDto getUserByEmail(String email);

    List<Long> getUserIdsByLanguage(String language);

    UserResponseDto updateUser(Long id, UserRequestDto request);

    void assignHostRole(Long userId);

    void assignTravelerRole(Long userId);
}
