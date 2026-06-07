package com.shareyourtrip.microservice.users.ShareYourTripUsersMs.services;

import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.ConversationRequestDto;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.ConversationResponseDto;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.MessageRequestDto;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.MessageResponseDto;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.entitites.Conversation;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.entitites.Message;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.entitites.Role;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.entitites.User;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.exceptions.ConversationNotFoundException;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.exceptions.UserNotFoundException;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.repositories.ConversationJpaRepository;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.repositories.MessageJpaRepository;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.repositories.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MessageServiceImpl - Tests unitarios")
class MessageServiceImplTest {

    @Mock
    private MessageJpaRepository messageRepository;

    @Mock
    private ConversationJpaRepository conversationRepository;

    @Mock
    private UserJpaRepository userRepository;

    @InjectMocks
    private MessageServiceImpl messageService;

    private User sender;
    private User recipient;
    private Conversation conversation;
    private Message message;

    @BeforeEach
    void setUp() {
        Role travelerRole = new Role((short) 2, "TRAVELER", new HashSet<>());

        sender = User.builder()
                .id(1L).firstName("Juan").lastName("García")
                .email("juan@example.com").passwordHash("hash1")
                .isActive(true).roles(new HashSet<>())
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();
        sender.getRoles().add(travelerRole);

        recipient = User.builder()
                .id(2L).firstName("Ana").lastName("López")
                .email("ana@example.com").passwordHash("hash2")
                .isActive(true).roles(new HashSet<>())
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        conversation = Conversation.builder()
                .id(1)
                .subject("Consulta sobre alojamiento")
                .build();

        message = Message.builder()
                .id(1)
                .conversation(conversation)
                .sender(sender)
                .recipient(recipient)
                .content("Hola, ¿está disponible?")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("createConversation")
    class CreateConversation {

        private ConversationRequestDto request;

        @BeforeEach
        void setUpRequest() {
            MessageRequestDto msgDto = new MessageRequestDto();
            msgDto.setSenderId(1L);
            msgDto.setRecipientId(2L);
            msgDto.setContent("Hola, ¿está disponible tu alojamiento?");

            request = new ConversationRequestDto();
            request.setSubject("Consulta sobre alojamiento");
            request.setMessage(msgDto);
        }

        @Test
        @DisplayName("Debe crear conversación con primer mensaje")
        void shouldCreateConversationWithMessage() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
            when(userRepository.findById(2L)).thenReturn(Optional.of(recipient));
            when(conversationRepository.save(any(Conversation.class))).thenAnswer(invocation -> {
                Conversation c = invocation.getArgument(0);
                c.setId(1);
                return c;
            });
            when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> {
                Message m = invocation.getArgument(0);
                m.setId(1);
                return m;
            });

            ConversationResponseDto result = messageService.createConversation(request);

            assertThat(result).isNotNull();
            assertThat(result.getSubject()).isEqualTo("Consulta sobre alojamiento");
            assertThat(result.getMessages()).hasSize(1);
            assertThat(result.getMessages().get(0).getContent()).isEqualTo("Hola, ¿está disponible tu alojamiento?");
            verify(conversationRepository).save(any(Conversation.class));
            verify(messageRepository).save(any(Message.class));
        }

        @Test
        @DisplayName("Debe lanzar UserNotFoundException cuando sender no existe")
        void shouldThrowWhenSenderNotFound() {
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> messageService.createConversation(request))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining("1");

            verify(conversationRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe lanzar UserNotFoundException cuando recipient no existe")
        void shouldThrowWhenRecipientNotFound() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
            when(userRepository.findById(2L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> messageService.createConversation(request))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining("2");

            verify(conversationRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("sendMessage")
    class SendMessage {

        private MessageRequestDto request;

        @BeforeEach
        void setUpRequest() {
            request = new MessageRequestDto();
            request.setConversationId(1);
            request.setSenderId(1L);
            request.setRecipientId(2L);
            request.setContent("Gracias por la info");
        }

        @Test
        @DisplayName("Debe enviar mensaje a una conversación existente")
        void shouldSendMessage() {
            when(conversationRepository.findById(1)).thenReturn(Optional.of(conversation));
            when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
            when(userRepository.findById(2L)).thenReturn(Optional.of(recipient));
            when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> {
                Message m = invocation.getArgument(0);
                m.setId(2);
                return m;
            });

            MessageResponseDto result = messageService.sendMessage(request);

            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEqualTo("Gracias por la info");
            assertThat(result.getConversationId()).isEqualTo(1);
            verify(messageRepository).save(any(Message.class));
        }

        @Test
        @DisplayName("Debe lanzar IllegalArgumentException cuando conversationId es null")
        void shouldThrowWhenConversationIdNull() {
            request.setConversationId(null);

            assertThatThrownBy(() -> messageService.sendMessage(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("conversación");

            verify(messageRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe lanzar ConversationNotFoundException cuando la conversación no existe")
        void shouldThrowWhenConversationNotFound() {
            when(conversationRepository.findById(99)).thenReturn(Optional.empty());

            request.setConversationId(99);

            assertThatThrownBy(() -> messageService.sendMessage(request))
                    .isInstanceOf(ConversationNotFoundException.class);

            verify(messageRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("getConversation")
    class GetConversation {

        @Test
        @DisplayName("Debe retornar conversación con sus mensajes")
        void shouldReturnConversationWithMessages() {
            when(conversationRepository.findById(1)).thenReturn(Optional.of(conversation));
            when(messageRepository.findByConversationIdOrderByCreatedAtAsc(1))
                    .thenReturn(List.of(message));

            List<ConversationResponseDto> result = messageService.getConversation(1);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getSubject()).isEqualTo("Consulta sobre alojamiento");
            assertThat(result.get(0).getMessages()).hasSize(1);
        }

        @Test
        @DisplayName("Debe lanzar ConversationNotFoundException cuando no existe")
        void shouldThrowWhenConversationNotFound() {
            when(conversationRepository.findById(99)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> messageService.getConversation(99))
                    .isInstanceOf(ConversationNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }

    @Nested
    @DisplayName("getConversationsByUser")
    class GetConversationsByUser {

        @Test
        @DisplayName("Debe retornar conversaciones del usuario")
        void shouldReturnUserConversations() {
            when(conversationRepository.findConversationsByUserId(1L))
                    .thenReturn(List.of(conversation));
            when(messageRepository.findByConversationIdOrderByCreatedAtAsc(1))
                    .thenReturn(List.of(message));

            List<ConversationResponseDto> result = messageService.getConversationsByUser(1L);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getSubject()).isEqualTo("Consulta sobre alojamiento");
        }

        @Test
        @DisplayName("Debe retornar lista vacía si el usuario no tiene conversaciones")
        void shouldReturnEmptyList() {
            when(conversationRepository.findConversationsByUserId(99L))
                    .thenReturn(List.of());

            List<ConversationResponseDto> result = messageService.getConversationsByUser(99L);

            assertThat(result).isEmpty();
        }
    }
}
