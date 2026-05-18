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
public class MessageRequestDto {
    private Integer conversationId;
    
    @NotNull(message = Messages.MANDATORY_USER_ID)
    private Long senderId;
    
    @NotNull(message = "El ID del destinatario es obligatorio")
    private Long recipientId;
    
    @NotBlank(message = Messages.MANDATORY_MESSAGE)
    private String content;
}
