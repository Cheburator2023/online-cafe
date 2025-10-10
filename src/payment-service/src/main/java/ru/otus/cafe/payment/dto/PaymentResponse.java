package ru.otus.cafe.payment.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
        Long id,
        Long orderId,
        Long userId,
        BigDecimal amount,
        String status,
        String paymentMethod,
        Instant createdAt
) {}