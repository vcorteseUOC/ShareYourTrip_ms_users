package com.shareyourtrip.microservice.users.ShareYourTripUsersMs.mappers;

import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.ConversationRequestDto;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.ConversationResponseDto;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.MessageResponseDto;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.entitites.Conversation;

import java.util.ArrayList;
import java.util.List;

public class ConversationMapper {
    public static Conversation toEntity(ConversationRequestDto request) {
        return Conversation.builder()
                .subject(request.getSubject())
                .build();
    }

    public static ConversationResponseDto toDTO(Conversation conversation, List<MessageResponseDto> messages) {
        return ConversationResponseDto.builder()
                .id(conversation.getId())
                .subject(conversation.getSubject())
                .messages(messages)
                .build();
    }

}
