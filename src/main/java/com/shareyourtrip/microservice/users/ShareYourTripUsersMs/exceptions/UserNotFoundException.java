package com.shareyourtrip.microservice.users.ShareYourTripUsersMs.exceptions;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }
}
