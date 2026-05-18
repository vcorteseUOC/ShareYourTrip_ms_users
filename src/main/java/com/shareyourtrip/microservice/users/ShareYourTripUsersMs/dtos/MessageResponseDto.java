package com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponseDto {

    private Integer id;
    private Integer conversationId;
    private Long senderId;
    private Long recipientId;
    private UserResponseDto sender;
    private UserResponseDto recipient;
    private String content;
    private LocalDateTime createdAt;
}
