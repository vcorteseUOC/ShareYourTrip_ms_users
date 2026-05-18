package com.shareyourtrip.microservice.users.ShareYourTripUsersMs.services;

import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.ConversationRequestDto;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.ConversationResponseDto;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.MessageRequestDto;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.MessageResponseDto;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.entitites.Conversation;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.entitites.Message;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.entitites.User;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.exceptions.ConversationNotFoundException;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.exceptions.Messages;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.exceptions.UserNotFoundException;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.mappers.ConversationMapper;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.mappers.MessageMapper;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.repositories.ConversationJpaRepository;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.repositories.MessageJpaRepository;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.repositories.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService{
    @Autowired
    private MessageJpaRepository messageRepository;
    @Autowired
    private ConversationJpaRepository conversationRepository;
    @Autowired
    private UserJpaRepository userRepository;


    @Override
    public ConversationResponseDto createConversation(ConversationRequestDto request) {
        User sender = userRepository.findById(request.getMessage().getSenderId())
                .orElseThrow(() -> new UserNotFoundException(Messages.USER_NOT_FOUND.concat(" con id: " + request.getMessage().getSenderId())));
        
        User recipient = userRepository.findById(request.getMessage().getRecipientId())
                .orElseThrow(() -> new UserNotFoundException(Messages.USER_NOT_FOUND.concat(" con id: " + request.getMessage().getRecipientId())));
        
        Conversation saved = conversationRepository.save(ConversationMapper.toEntity(request));
        Message messageSaved = messageRepository.save(MessageMapper.toEntity(saved, sender, recipient, request.getMessage()));
        
        List<MessageResponseDto> messages = List.of(MessageMapper.toDto(messageSaved));
        
        return ConversationMapper.toDTO(saved, messages);
    }

    @Override
    public MessageResponseDto sendMessage(MessageRequestDto request) {
        if (request.getConversationId() == null) {
            throw new IllegalArgumentException(Messages.MANDATORY_CONVERSATION_ID);
        }

        Conversation conversation = conversationRepository.findById(request.getConversationId())
                .orElseThrow(() -> new ConversationNotFoundException(Messages.CONVERSATION_NOT_FOUND));

        User sender = userRepository.findById(request.getSenderId())
                .orElseThrow(() -> new UserNotFoundException(Messages.USER_NOT_FOUND.concat(" con id: " + request.getSenderId())));

        User recipient = userRepository.findById(request.getRecipientId())
                .orElseThrow(() -> new UserNotFoundException(Messages.USER_NOT_FOUND.concat(" con id: " + request.getRecipientId())));

        Message message = MessageMapper.toEntity(conversation, sender, recipient, request);
        Message saved = messageRepository.save(message);

        return MessageMapper.toDto(saved);
    }

    @Override
    public List<ConversationResponseDto> getConversation(Integer conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ConversationNotFoundException(Messages.CONVERSATION_NOT_FOUND.concat(" con id: " + conversationId)));

        List<Message> messages = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);

        List<MessageResponseDto> messageDtos = messages.stream()
                .map(MessageMapper::toDto)
                .toList();

        ConversationResponseDto conversationDto = ConversationMapper.toDTO(conversation, messageDtos);

        return List.of(conversationDto);
    }

    @Override
    public List<ConversationResponseDto> getConversationsByUser(Long userId) {
        List<Conversation> conversations = conversationRepository.findConversationsByUserId(userId);

        return conversations.stream()
                .map(conversation -> {
                    List<Message> messages = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversation.getId());
                    List<MessageResponseDto> messageDtos = messages.stream()
                            .map(MessageMapper::toDto)
                            .toList();
                    return ConversationMapper.toDTO(conversation, messageDtos);
                })
                .toList();
    }
}
