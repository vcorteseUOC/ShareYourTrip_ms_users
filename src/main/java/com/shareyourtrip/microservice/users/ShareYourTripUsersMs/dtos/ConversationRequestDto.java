package com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos;

import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.exceptions.Messages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConversationRequestDto {
    @NotBlank(message = Messages.MANDATORY_SUBJECT)
    private String subject;
    
    @NotNull(message = Messages.MANDATORY_MESSAGE)
    private MessageRequestDto message;
}
