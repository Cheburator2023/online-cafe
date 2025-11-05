package ru.otus.cafe.common.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderCreatedEvent(
        Long orderId,
        Long userId,
        BigDecimal totalAmount,
        List<OrderItemEvent> items,
        Instant createdAt
) {}

record OrderItemEvent(
        Long menuItemId,
        String menuItemName,
        Integer quantity,
        BigDecimal unitPrice
) {}