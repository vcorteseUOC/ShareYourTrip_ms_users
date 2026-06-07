package com.shareyourtrip.microservice.users.ShareYourTripUsersMs.services;

import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.LoginRequestDto;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.LoginResponseDto;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.RegisterRequestDto;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.UserResponseDto;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.entitites.Role;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.entitites.User;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.exceptions.UnauthorizedException;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.repositories.UserJpaRepository;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthServiceImpl - Tests unitarios")
class AuthServiceImplTest {

    @Mock
    private UserJpaRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    private User activeUser;
    private Role travelerRole;

    @BeforeEach
    void setUp() {
        travelerRole = new Role((short) 2, "TRAVELER", new HashSet<>());

        activeUser = User.builder()
                .id(1L)
                .firstName("Juan")
                .lastName("García")
                .email("juan@example.com")
                .passwordHash("hashedPassword")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .roles(new HashSet<>(Set.of(travelerRole)))
                .build();
    }

    @Nested
    @DisplayName("login")
    class Login {

        private LoginRequestDto loginRequest;

        @BeforeEach
        void setUpRequest() {
            loginRequest = new LoginRequestDto("juan@example.com", "password123");
        }

        @Test
        @DisplayName("Debe retornar LoginResponseDto con token cuando credenciales son válidas")
        void shouldReturnTokenOnValidCredentials() {
            when(userRepository.findByEmail("juan@example.com")).thenReturn(Optional.of(activeUser));
            when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);
            when(jwtUtil.generateToken(eq(1L), eq("juan@example.com"), anyList())).thenReturn("jwt-token-123");

            LoginResponseDto result = authService.login(loginRequest);

            assertThat(result).isNotNull();
            assertThat(result.getToken()).isEqualTo("jwt-token-123");
            assertThat(result.getEmail()).isEqualTo("juan@example.com");
            assertThat(result.getFirstName()).isEqualTo("Juan");
            assertThat(result.getRoles()).containsExactly("TRAVELER");
            assertThat(result.getMessage()).isEqualTo("Login correcto");
        }

        @Test
        @DisplayName("Debe lanzar UnauthorizedException cuando el email no existe")
        void shouldThrowWhenEmailNotFound() {
            when(userRepository.findByEmail("noexiste@example.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.login(
                    new LoginRequestDto("noexiste@example.com", "password123")))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessageContaining("Credenciales inválidas");

            verify(jwtUtil, never()).generateToken(anyLong(), anyString(), anyList());
        }

        @Test
        @DisplayName("Debe lanzar UnauthorizedException cuando el password es incorrecto")
        void shouldThrowWhenPasswordIncorrect() {
            when(userRepository.findByEmail("juan@example.com")).thenReturn(Optional.of(activeUser));
            when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

            assertThatThrownBy(() -> authService.login(
                    new LoginRequestDto("juan@example.com", "wrongPassword")))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessageContaining("Credenciales inválidas");
        }

        @Test
        @DisplayName("Debe lanzar UnauthorizedException cuando el usuario está inactivo")
        void shouldThrowWhenUserInactive() {
            activeUser.setIsActive(false);
            when(userRepository.findByEmail("juan@example.com")).thenReturn(Optional.of(activeUser));

            assertThatThrownBy(() -> authService.login(loginRequest))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessageContaining("inactivo");

            verify(passwordEncoder, never()).matches(anyString(), anyString());
        }
    }

    @Nested
    @DisplayName("register")
    class Register {

        private RegisterRequestDto registerRequest;

        @BeforeEach
        void setUpRequest() {
            registerRequest = new RegisterRequestDto();
            registerRequest.setFirstName("Nuevo");
            registerRequest.setLastName("Usuario");
            registerRequest.setEmail("nuevo@example.com");
            registerRequest.setPassword("password123");
            registerRequest.setPhone("+34600000000");
            registerRequest.setBirthDate("1995-05-15");
            registerRequest.setLanguage("es");
            registerRequest.setBio("Viajero");
        }

        @Test
        @DisplayName("Debe crear usuario con rol TRAVELER por defecto")
        void shouldCreateUserWithTravelerRole() {
            when(userRepository.findByEmail("nuevo@example.com")).thenReturn(Optional.empty());
            when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                user.setId(10L);
                return user;
            });

            UserResponseDto result = authService.register(registerRequest);

            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo("nuevo@example.com");
            assertThat(result.getRoles()).containsExactly("TRAVELER");
            verify(passwordEncoder).encode("password123");
            verify(userRepository).save(argThat(user ->
                    user.getRoles().stream().anyMatch(r -> r.getName().equals("TRAVELER"))
                            && user.getLanguage().equals("ES") // toUpperCase
            ));
        }

        @Test
        @DisplayName("Debe lanzar IllegalArgumentException cuando el email ya está registrado")
        void shouldThrowWhenEmailAlreadyRegistered() {
            when(userRepository.findByEmail("nuevo@example.com")).thenReturn(Optional.of(activeUser));

            assertThatThrownBy(() -> authService.register(registerRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("email ya está registrado");

            verify(userRepository, never()).save(any());
        }
    }
}
