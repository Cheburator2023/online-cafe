package ru.otus.cafe.order.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        Long userId,
        String status,
        BigDecimal totalAmount,
        String specialInstructions,
        List<OrderItemResponse> items,
        Instant createdAt
) {}
