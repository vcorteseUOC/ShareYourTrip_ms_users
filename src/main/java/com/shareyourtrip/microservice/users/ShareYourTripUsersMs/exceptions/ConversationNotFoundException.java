package com.shareyourtrip.microservice.users.ShareYourTripUsersMs.exceptions;

public class ConversationNotFoundException extends RuntimeException {
    public ConversationNotFoundException(String message) {
        super(message);
    }
}
