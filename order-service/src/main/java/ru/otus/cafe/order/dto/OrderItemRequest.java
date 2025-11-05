package ru.otus.cafe.order.dto;

import jakarta.validation.constraints.NotNull;

public record OrderItemRequest(
        @NotNull Long menuItemId,
        @NotNull Integer quantity
) {}
