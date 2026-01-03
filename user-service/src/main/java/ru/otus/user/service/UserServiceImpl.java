package ru.otus.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.user.dto.UserRequest;
import ru.otus.user.dto.UserResponse;
import ru.otus.user.event.UserCreatedEvent;
import ru.otus.user.event.UserUpdatedEvent;
import ru.otus.user.exception.UserNotFoundException;
import ru.otus.user.mapper.UserMapper;
import ru.otus.user.model.User;
import ru.otus.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public UserResponse createUser(UserRequest userRequest) {
        log.info("Creating user with email: {}", userRequest.email());

        // Проверка уникальности email
        userRepository.findByEmail(userRequest.email())
                .ifPresent(user -> {
                    throw new DataIntegrityViolationException("Email already exists: " + userRequest.email());
                });

        User user = new User(userRequest.name(), userRequest.email());
        User savedUser = userRepository.save(user);

        // Публикация события
        UserCreatedEvent event = userMapper.toCreatedEvent(savedUser);
        eventPublisher.publishEvent(event);
        log.info("Published UserCreatedEvent for user ID: {}", savedUser.getId());

        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        log.debug("Fetching user by ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        log.info("Updating user ID: {}", id);

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        // Проверка уникальности email (исключая текущего пользователя)
        if (!existingUser.getEmail().equals(userRequest.email()) &&
                userRepository.existsByEmailAndIdNot(userRequest.email(), id)) {
            throw new DataIntegrityViolationException("Email already exists: " + userRequest.email());
        }

        User oldUser = new User(existingUser.getName(), existingUser.getEmail());
        oldUser.setId(existingUser.getId());

        existingUser.setName(userRequest.name());
        existingUser.setEmail(userRequest.email());
        User updatedUser = userRepository.save(existingUser);

        // Публикация события
        UserUpdatedEvent event = userMapper.toUpdatedEvent(oldUser, updatedUser);
        eventPublisher.publishEvent(event);
        log.info("Published UserUpdatedEvent for user ID: {}", updatedUser.getId());

        return userMapper.toResponse(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user ID: {}", id);
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
        log.debug("User ID: {} deleted successfully", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        log.debug("Fetching all users");
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
    }
}