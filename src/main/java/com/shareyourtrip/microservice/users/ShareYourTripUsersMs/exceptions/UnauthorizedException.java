package com.shareyourtrip.microservice.users.ShareYourTripUsersMs.exceptions;

public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
