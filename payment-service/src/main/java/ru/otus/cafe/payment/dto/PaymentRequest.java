package ru.otus.cafe.payment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PaymentRequest(
        @NotNull Long orderId,
        @NotNull Long userId,
        @NotNull @Positive BigDecimal amount,
        String paymentMethod
) {}