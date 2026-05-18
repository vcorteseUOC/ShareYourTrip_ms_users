package com.shareyourtrip.microservice.users.ShareYourTripUsersMs.controllers;

import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.ConversationRequestDto;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.ConversationResponseDto;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.MessageRequestDto;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.MessageResponseDto;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.services.MessageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/conversations")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping
    public ConversationResponseDto createConversation(@Valid @RequestBody ConversationRequestDto request) {
        return messageService.createConversation(request);
    }

    @PostMapping("/message")
    public MessageResponseDto sendMessage(@Valid @RequestBody MessageRequestDto request) {
        return messageService.sendMessage(request);
    }

    @GetMapping("/{id}")
    public List<ConversationResponseDto> getConversation(@PathVariable Integer id) {
        return messageService.getConversation(id);
    }

    @GetMapping("/user/{userId}")
    public List<ConversationResponseDto> getConversationsByUser(@PathVariable Long userId) {
        return messageService.getConversationsByUser(userId);
    }
}
