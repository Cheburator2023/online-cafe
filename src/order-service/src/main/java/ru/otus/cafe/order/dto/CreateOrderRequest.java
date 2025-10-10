package ru.otus.cafe.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateOrderRequest(
        @NotNull Long userId,
        @NotEmpty List<@Valid OrderItemRequest> items,
        String specialInstructions
) {}

record OrderItemRequest(
        @NotNull Long menuItemId,
        @NotNull Integer quantity
) {}