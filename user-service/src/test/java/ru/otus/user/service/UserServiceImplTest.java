package ru.otus.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import ru.otus.user.dto.UserRequest;
import ru.otus.user.dto.UserResponse;
import ru.otus.user.exception.UserNotFoundException;
import ru.otus.user.mapper.UserMapper;
import ru.otus.user.model.User;
import ru.otus.user.repository.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserRequest testUserRequest;
    private UserResponse testUserResponse;

    @BeforeEach
    void setUp() {
        testUser = new User("John Doe", "john@example.com");
        testUserRequest = new UserRequest("John Doe", "john@example.com");
        testUserResponse = new UserResponse(1L, "John Doe", "john@example.com", Instant.now());
    }

    @Test
    @Disabled
    void createUser_ShouldCreateAndReturnUser() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toResponse(any(User.class))).thenReturn(testUserResponse);

        UserResponse result = userService.createUser(testUserRequest);

        assertNotNull(result);
        assertEquals("John Doe", result.name());
        verify(userRepository).save(any(User.class));
        verify(eventPublisher).publishEvent(any());
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.toResponse(testUser)).thenReturn(testUserResponse);

        UserResponse result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_WhenUserNotExists_ShouldThrowException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(999L));
    }

    @Test
    @Disabled
    void updateUser_ShouldUpdateAndReturnUser() {
        User existingUser = new User("Old Name", "old@example.com");
        existingUser.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmailAndIdNot(any(), any())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toResponse(any(User.class))).thenReturn(testUserResponse);

        UserResponse result = userService.updateUser(1L, testUserRequest);

        assertNotNull(result);
        assertEquals("john@example.com", result.email());
        verify(userRepository).save(any(User.class));
        verify(eventPublisher).publishEvent(any());
    }

    @Test
    void deleteUser_WhenUserExists_ShouldDeleteUser() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_WhenUserNotExists_ShouldThrowException() {
        when(userRepository.existsById(999L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(999L));
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(testUser));
        when(userMapper.toResponse(testUser)).thenReturn(testUserResponse);

        List<UserResponse> results = userService.getAllUsers();

        assertEquals(1, results.size());
        verify(userRepository).findAll();
    }
}