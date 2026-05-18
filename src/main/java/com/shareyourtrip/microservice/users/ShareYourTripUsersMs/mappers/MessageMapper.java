package com.shareyourtrip.microservice.users.ShareYourTripUsersMs.mappers;

import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.MessageRequestDto;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.MessageResponseDto;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.entitites.Conversation;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.entitites.Message;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.entitites.User;

import java.time.LocalDateTime;

public class MessageMapper {
    public static Message toEntity(Conversation conversation, User sender, User recipient, MessageRequestDto request) {
        return Message.builder()
                .conversation(conversation)
                .sender(sender)
                .recipient(recipient)
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static MessageResponseDto toDto(Message message) {
        return MessageResponseDto.builder()
                .id(message.getId())
                .conversationId(message.getConversation().getId())
                .senderId(message.getSender().getId())
                .recipientId(message.getRecipient().getId())
                .sender(UsersMapper.toDto(message.getSender()))
                .recipient(UsersMapper.toDto(message.getRecipient()))
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
