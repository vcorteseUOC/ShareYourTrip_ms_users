package com.shareyourtrip.microservice.users.ShareYourTripUsersMs.services;

import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.ConversationRequestDto;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.ConversationResponseDto;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.MessageRequestDto;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.MessageResponseDto;

import java.util.List;

public interface MessageService {
    ConversationResponseDto createConversation(ConversationRequestDto request);

    MessageResponseDto sendMessage(MessageRequestDto request);

    List<ConversationResponseDto> getConversation(Integer conversationId);

    List<ConversationResponseDto> getConversationsByUser(Long userId);
}
