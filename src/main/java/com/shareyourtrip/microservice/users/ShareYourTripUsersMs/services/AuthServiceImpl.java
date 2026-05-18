package com.shareyourtrip.microservice.users.ShareYourTripUsersMs.services;

import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.LoginRequestDto;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.LoginResponseDto;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.RegisterRequestDto;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.UserResponseDto;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.entitites.User;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.entitites.Role;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.exceptions.Messages;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.exceptions.UnauthorizedException;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.mappers.AuthMapper;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.mappers.UsersMapper;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.repositories.UserJpaRepository;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.util.JwtUtil;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Log
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserJpaRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

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

        List<String> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), roles);

        return new LoginResponseDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getProfilePhotoUrl(),
                user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()),
                Messages.LOGIN_OK,
                token
        );
    }

    @Override
    public UserResponseDto register(RegisterRequestDto request) {
        // Verificar si el email ya existe
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // Crear el nuevo usuario
        User user = AuthMapper.toEntity(request, passwordEncoder);

        // Asignar rol por defecto (TRAVELER)
        Set<Role> roles = new HashSet<>();
        Role travelerRole = new Role();
        travelerRole.setId((short) 2);
        travelerRole.setName("TRAVELER");
        roles.add(travelerRole);
        user.setRoles(roles);

        // Guardar el usuario
        User savedUser = userRepository.save(user);

        return UsersMapper.toDto(savedUser);
    }
}

