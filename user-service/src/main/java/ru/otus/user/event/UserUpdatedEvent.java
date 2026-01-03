package ru.otus.user.event;

import java.time.Instant;

public record UserUpdatedEvent(
        Long userId,
        String oldEmail,
        String newEmail,
        String oldName,
        String newName,
        Instant updatedAt
) {}