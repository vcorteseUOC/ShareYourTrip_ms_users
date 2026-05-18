package com.shareyourtrip.microservice.users.ShareYourTripUsersMs.controllers;

import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.UserRequestDto;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.UserResponseDto;
import jakarta.validation.Valid;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.services.UserService;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.exceptions.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserResponseDto getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/email/{email}")
    public UserResponseDto getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }

    @GetMapping("/lan/{language}")
    public List<Long> getUserByLanguage(@PathVariable String language) {
        return userService.getUserIdsByLanguage(language);
    }

    @PutMapping("/{id}")
    public UserResponseDto updateUser(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String xUserId,
            @Valid @RequestBody UserRequestDto request) {
        if (!id.equals(Long.parseLong(xUserId))) {
            throw new UnauthorizedException("No tienes permiso para modificar este usuario");
        }
        return userService.updateUser(id, request);
    }

    @PostMapping("/{id}/role/host")
    public void assignHostRole(@PathVariable Long id) {
        userService.assignHostRole(id);
    }

    @PostMapping("/{id}/role/traveler")
    public void assignTravelerRole(@PathVariable Long id) {
        userService.assignTravelerRole(id);
    }
}
