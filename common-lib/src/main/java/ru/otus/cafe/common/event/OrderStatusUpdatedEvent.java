package ru.otus.cafe.common.event;

import java.time.Instant;

public record OrderStatusUpdatedEvent(
        Long orderId,
        Long userId,
        String newStatus,
        String oldStatus,
        Instant updatedAt
) {}
