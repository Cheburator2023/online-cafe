package ru.otus.user.mapper;

import org.springframework.stereotype.Component;
import ru.otus.user.dto.UserResponse;
import ru.otus.user.event.UserCreatedEvent;
import ru.otus.user.event.UserUpdatedEvent;
import ru.otus.user.model.User;

@Component
public class UserMapper {
    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }

    public UserCreatedEvent toCreatedEvent(User user) {
        return new UserCreatedEvent(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getCreatedAt()
        );
    }

    public UserUpdatedEvent toUpdatedEvent(User oldUser, User newUser) {
        return new UserUpdatedEvent(
                oldUser.getId(),
                oldUser.getEmail(),
                newUser.getEmail(),
                oldUser.getName(),
                newUser.getName(),
                newUser.getCreatedAt()
        );
    }
}