package com.shareyourtrip.microservice.users.ShareYourTripUsersMs.services;

import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.UserResponseDto;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.entitites.User;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.exceptions.Messages;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.exceptions.UserNotFoundException;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.mappers.UsersMapper;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.repositories.UserJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Arrays.stream;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserJpaRepository userRepository;

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UsersMapper::toDto)
                .toList();
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(Messages.USER_NOT_FOUND.concat(" con id: " + id)));

        return UsersMapper.toDto(user);
    }

    @Override
    public UserResponseDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(Messages.USER_NOT_FOUND.concat(" con email: " + email)));

        return UsersMapper.toDto(user);
    }

    @Override
    public List<Long> getUserIdsByLanguage(String language) {
        return userRepository.getUserIdsByLanguage(language);
    }
}