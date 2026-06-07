package com.shareyourtrip.microservice.users.ShareYourTripUsersMs.services;

import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.UserRequestDto;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos.UserResponseDto;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.entitites.Role;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.entitites.User;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.exceptions.UserNotFoundException;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.repositories.RoleJpaRepository;
import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.repositories.UserJpaRepository;
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
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl - Tests unitarios")
class UserServiceImplTest {

    @Mock
    private UserJpaRepository userRepository;

    @Mock
    private RoleJpaRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User sampleUser;
    private Role travelerRole;
    private Role hostRole;

    @BeforeEach
    void setUp() {
        travelerRole = new Role((short) 2, "TRAVELER", new HashSet<>());
        hostRole = new Role((short) 1, "HOST", new HashSet<>());

        sampleUser = User.builder()
                .id(1L)
                .firstName("Juan")
                .lastName("García")
                .email("juan@example.com")
                .passwordHash("hashedPassword")
                .phone("+34600000000")
                .bio("Viajero apasionado")
                .language("es")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .birthDate(LocalDate.of(1995, 5, 15))
                .roles(new HashSet<>(Set.of(travelerRole)))
                .build();
    }

    @Nested
    @DisplayName("getAllUsers")
    class GetAllUsers {

        @Test
        @DisplayName("Debe retornar lista de UserResponseDto")
        void shouldReturnListOfDtos() {
            User user2 = User.builder()
                    .id(2L).firstName("Ana").lastName("López")
                    .email("ana@example.com").passwordHash("hash2")
                    .roles(new HashSet<>(Set.of(hostRole)))
                    .isActive(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            when(userRepository.findAll()).thenReturn(List.of(sampleUser, user2));

            List<UserResponseDto> result = userService.getAllUsers();

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getFirstName()).isEqualTo("Juan");
            assertThat(result.get(1).getFirstName()).isEqualTo("Ana");
            verify(userRepository).findAll();
        }

        @Test
        @DisplayName("Debe retornar lista vacía si no hay usuarios")
        void shouldReturnEmptyList() {
            when(userRepository.findAll()).thenReturn(List.of());

            List<UserResponseDto> result = userService.getAllUsers();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getUserById")
    class GetUserById {

        @Test
        @DisplayName("Debe retornar DTO cuando el usuario existe")
        void shouldReturnDtoWhenUserExists() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

            UserResponseDto result = userService.getUserById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getFirstName()).isEqualTo("Juan");
            assertThat(result.getEmail()).isEqualTo("juan@example.com");
            assertThat(result.getRoles()).containsExactly("TRAVELER");
        }

        @Test
        @DisplayName("Debe lanzar UserNotFoundException cuando no existe")
        void shouldThrowWhenUserNotFound() {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getUserById(99L))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining("99");

            verify(userRepository).findById(99L);
        }
    }

    @Nested
    @DisplayName("getUserByEmail")
    class GetUserByEmail {

        @Test
        @DisplayName("Debe retornar DTO cuando el email existe")
        void shouldReturnDtoWhenEmailExists() {
            when(userRepository.findByEmail("juan@example.com")).thenReturn(Optional.of(sampleUser));

            UserResponseDto result = userService.getUserByEmail("juan@example.com");

            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo("juan@example.com");
        }

        @Test
        @DisplayName("Debe lanzar UserNotFoundException cuando el email no existe")
        void shouldThrowWhenEmailNotFound() {
            when(userRepository.findByEmail("noexiste@example.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getUserByEmail("noexiste@example.com"))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining("noexiste@example.com");
        }
    }

    @Nested
    @DisplayName("getUserIdsByLanguage")
    class GetUserIdsByLanguage {

        @Test
        @DisplayName("Debe retornar lista de IDs de usuarios por idioma")
        void shouldReturnUserIds() {
            when(userRepository.getUserIdsByLanguage("es")).thenReturn(List.of(1L, 2L, 3L));

            List<Long> result = userService.getUserIdsByLanguage("es");

            assertThat(result).containsExactly(1L, 2L, 3L);
        }

        @Test
        @DisplayName("Debe retornar lista vacía si no hay usuarios con ese idioma")
        void shouldReturnEmptyList() {
            when(userRepository.getUserIdsByLanguage("zh")).thenReturn(List.of());

            List<Long> result = userService.getUserIdsByLanguage("zh");

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("updateUser")
    class UpdateUser {

        private UserRequestDto requestDto;

        @BeforeEach
        void setUpRequest() {
            requestDto = new UserRequestDto();
            requestDto.setFirstName("Juan Updated");
            requestDto.setLastName("García Updated");
            requestDto.setPhone("+34611111111");
            requestDto.setBio("Bio actualizada");
            requestDto.setLanguage("en");
            requestDto.setPassword("newPassword123");
        }

        @Test
        @DisplayName("Debe actualizar campos y codificar password")
        void shouldUpdateFieldsAndEncodePassword() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
            when(passwordEncoder.encode("newPassword123")).thenReturn("newHashedPassword");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            UserResponseDto result = userService.updateUser(1L, requestDto);

            assertThat(result.getFirstName()).isEqualTo("Juan Updated");
            assertThat(result.getLastName()).isEqualTo("García Updated");
            assertThat(result.getLanguage()).isEqualTo("EN"); // toUpperCase
            verify(passwordEncoder).encode("newPassword123");
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("No debe codificar password cuando es null o blank")
        void shouldNotEncodeBlankPassword() {
            requestDto.setPassword("");
            when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            userService.updateUser(1L, requestDto);

            verify(passwordEncoder, never()).encode(anyString());
        }

        @Test
        @DisplayName("Debe lanzar UserNotFoundException si usuario no existe")
        void shouldThrowWhenUserNotFound() {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.updateUser(99L, requestDto))
                    .isInstanceOf(UserNotFoundException.class);

            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("assignHostRole")
    class AssignHostRole {

        @Test
        @DisplayName("Debe añadir rol HOST cuando el usuario no lo tiene")
        void shouldAddHostRole() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
            when(roleRepository.findByName("HOST")).thenReturn(Optional.of(hostRole));
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            userService.assignHostRole(1L);

            assertThat(sampleUser.getRoles()).contains(hostRole);
            verify(userRepository).save(sampleUser);
        }

        @Test
        @DisplayName("No debe duplicar rol HOST si ya lo tiene")
        void shouldNotDuplicateHostRole() {
            sampleUser.setRoles(new HashSet<>(Set.of(hostRole)));
            when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
            when(roleRepository.findByName("HOST")).thenReturn(Optional.of(hostRole));

            userService.assignHostRole(1L);

            assertThat(sampleUser.getRoles().stream().filter(r -> r.getName().equals("HOST"))).hasSize(1);
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe lanzar UserNotFoundException si usuario no existe")
        void shouldThrowWhenUserNotFound() {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.assignHostRole(99L))
                    .isInstanceOf(UserNotFoundException.class);
        }

        @Test
        @DisplayName("Debe lanzar IllegalStateException si rol HOST no existe en BD")
        void shouldThrowWhenHostRoleNotFound() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
            when(roleRepository.findByName("HOST")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.assignHostRole(1L))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("HOST");
        }
    }

    @Nested
    @DisplayName("assignTravelerRole")
    class AssignTravelerRole {

        @Test
        @DisplayName("Debe reemplazar rol HOST por TRAVELER")
        void shouldReplaceHostWithTraveler() {
            sampleUser.setRoles(new HashSet<>(Set.of(hostRole)));
            when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
            when(roleRepository.findByName("TRAVELER")).thenReturn(Optional.of(travelerRole));
            when(roleRepository.findByName("HOST")).thenReturn(Optional.of(hostRole));
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            userService.assignTravelerRole(1L);

            assertThat(sampleUser.getRoles()).contains(travelerRole);
            assertThat(sampleUser.getRoles()).doesNotContain(hostRole);
            verify(userRepository).save(sampleUser);
        }

        @Test
        @DisplayName("No debe duplicar rol TRAVELER si ya lo tiene")
        void shouldNotDuplicateTravelerRole() {
            sampleUser.setRoles(new HashSet<>(Set.of(travelerRole)));
            when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
            when(roleRepository.findByName("TRAVELER")).thenReturn(Optional.of(travelerRole));
            when(roleRepository.findByName("HOST")).thenReturn(Optional.of(hostRole));
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            userService.assignTravelerRole(1L);

            assertThat(sampleUser.getRoles().stream().filter(r -> r.getName().equals("TRAVELER"))).hasSize(1);
        }

        @Test
        @DisplayName("Debe lanzar UserNotFoundException si usuario no existe")
        void shouldThrowWhenUserNotFound() {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.assignTravelerRole(99L))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }
}
