package com.shareyourtrip.microservice.users.ShareYourTripUsersMs.services;

import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.LoginRequestDto;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.LoginResponseDto;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.entitites.User;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.entitites.Role;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.exceptions.Messages;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.exceptions.UnauthorizedException;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.repositories.UserJpaRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Log
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserJpaRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public LoginResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas"));

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new UnauthorizedException("El usuario está inactivo");
        }

        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash()
        );

        if (!passwordMatches) {
            log.info("Password should be " + passwordEncoder.encode("123456"));
            throw new UnauthorizedException("Credenciales inválidas");
        }

        Set<String> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return new LoginResponseDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                roles,
                Messages.LOGIN_OK
        );
    }
}

