package com.shareyourtrip.microservice.users.ShareYourTripUsersMs.controllers;

import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.UserResponseDto;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.services.UserService;
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
}

