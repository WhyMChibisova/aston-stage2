package ru.aston.hometask.userservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.aston.hometask.userservice.dao.UserRepository;
import ru.aston.hometask.userservice.dto.UserDto;
import ru.aston.hometask.userservice.exception.BadRequestException;
import ru.aston.hometask.userservice.exception.ResourceNotFoundException;
import ru.aston.hometask.userservice.model.User;
import ru.aston.hometask.userservice.producer.UserEventProducerService;
import ru.aston.hometask.userservice.service.impl.UserServiceImpl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private static final String USER_NOT_FOUND_MSG = "User not found: %s";
    private static final String EMAIL_DUPLICATE_MSG = "User email already exists: %s";

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserEventProducerService producerService;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserDto userDTO;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        testUser = User.builder()
                .id(userId)
                .name("Masha")
                .email("test@test.com")
                .age(23)
                .build();

        userDTO = UserDto.builder()
                .name("Masha")
                .email("test@test.com")
                .age(23)
                .build();
    }

    @Test
    void saveUser_whenDataIsValid() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserDto savedUser = userService.create(userDTO);

        assertNotNull(savedUser);
        assertEquals(userDTO, savedUser);

        verify(userRepository, times(1)).findByEmail("test@test.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void saveUser_whenEmailAlreadyExists_thenThrowException() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> userService.create(userDTO));
        assertEquals(String.format(EMAIL_DUPLICATE_MSG, userDTO.email()), exception.getMessage());

        verify(userRepository, times(1)).findByEmail("test@test.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void findUserById_whenUserExists_thenReturnUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        UserDto foundUser = userService.getById(userId);

        assertNotNull(foundUser);
        assertEquals(userDTO, foundUser);

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void findUserById_whenUserNotExists_thenReturnEmpty() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> userService.getById(userId));
        assertEquals(String.format(USER_NOT_FOUND_MSG, userId), exception.getMessage());
    }

    @Test
    void findAllUsers_whenUsersExist() {
        List<User> users = List.of(
                testUser,
                User.builder().id(UUID.randomUUID()).name("Dima").email("test2@test.com").age(22).build()
        );
        when(userRepository.findAll()).thenReturn(users);

        List<UserDto> actualUsers = userService.getAll();

        assertEquals(2, actualUsers.size());
        assertEquals(userDTO, actualUsers.get(0));

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void findAllUsers_whenUsersNotExist() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserDto> actualUsers = userService.getAll();

        assertEquals(0, actualUsers.size());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void updateUser_whenDataIsValid() {
        User updatedUser = User.builder()
                .id(userId)
                .name("Masha2")
                .email("test2@test.com")
                .age(23)
                .build();
        UserDto updatedUserDto = UserDto.builder()
                .name("Masha2")
                .email("test2@test.com")
                .age(23)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail("test2@test.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDto actualUser = userService.update(userId, updatedUserDto);

        assertEquals(updatedUserDto, actualUser);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).findByEmail("test2@test.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_whenSameEmails() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.update(userId, userDTO);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).findByEmail("test@test.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_whenEmailBelongsToAnotherUser_thenThrowException() {
        User anotherUser = User.builder()
                .id(UUID.randomUUID())
                .name("Masha2")
                .email("test2@test.com")
                .age(23)
                .build();
        UserDto updatedUserDto = UserDto.builder()
                .name("Masha2")
                .email("test2@test.com")
                .age(23)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail("test2@test.com")).thenReturn(Optional.of(anotherUser));

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> userService.update(userId, updatedUserDto));
        assertEquals(String.format(EMAIL_DUPLICATE_MSG, updatedUserDto.email()), exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).findByEmail("test2@test.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_whenUserNonExists_thenThrowException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> userService.update(userId, userDTO));
        assertEquals(String.format(USER_NOT_FOUND_MSG, userId), exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).findByEmail(any());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_whenUserExists() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).deleteById(userId);

        userService.delete(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void deleteUser_whenIdIsInvalid_thenThrowException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> userService.delete(userId));
        assertEquals(String.format(USER_NOT_FOUND_MSG, userId), exception.getMessage());

        verify(userRepository, never()).deleteById(any());
    }
}