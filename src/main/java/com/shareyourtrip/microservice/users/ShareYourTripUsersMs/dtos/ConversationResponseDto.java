package com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationResponseDto {
    private Integer id;
    private String subject;
    private List<MessageResponseDto> messages;
}
