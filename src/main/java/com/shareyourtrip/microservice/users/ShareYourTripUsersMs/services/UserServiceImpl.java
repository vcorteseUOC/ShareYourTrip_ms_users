package com.shareyourtrip.microservice.users.ShareYourTripUsersMs.services;

import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.UserRequestDto;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.UserResponseDto;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.entitites.Role;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.entitites.User;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.exceptions.Messages;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.exceptions.UserNotFoundException;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.mappers.UsersMapper;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.repositories.RoleJpaRepository;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.repositories.UserJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Arrays.stream;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleJpaRepository roleRepository;

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

    @Override
    public UserResponseDto updateUser(Long id, UserRequestDto request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(Messages.USER_NOT_FOUND.concat(" con id: " + id)));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setProfilePhotoUrl(request.getProfilePhotoUrl());
        user.setBio(request.getBio());
        user.setLanguage(request.getLanguage().toUpperCase());
        user.setBirthDate(request.getBirthDate());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        return UsersMapper.toDto(userRepository.save(user));
    }

    @Override
    public void assignHostRole(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(Messages.USER_NOT_FOUND.concat(" con id: " + userId)));
        Role hostRole = roleRepository.findByName("HOST")
                .orElseThrow(() -> new IllegalStateException("Rol HOST no encontrado"));
        if (!user.getRoles().contains(hostRole)) {
            user.getRoles().add(hostRole);
            userRepository.save(user);
        }
    }

    @Override
    public void assignTravelerRole(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(Messages.USER_NOT_FOUND.concat(" con id: " + userId)));
        Role travelerRole = roleRepository.findByName("TRAVELER")
                .orElseThrow(() -> new IllegalStateException("Rol TRAVELER no encontrado"));
        Role hostRole = roleRepository.findByName("HOST")
                .orElseThrow(() -> new IllegalStateException("Rol HOST no encontrado"));
        if (user.getRoles().contains(hostRole)) {
            user.getRoles().remove(hostRole);
        }
        if (!user.getRoles().contains(travelerRole)) {
            user.getRoles().add(travelerRole);
        }
        userRepository.save(user);
    }
}