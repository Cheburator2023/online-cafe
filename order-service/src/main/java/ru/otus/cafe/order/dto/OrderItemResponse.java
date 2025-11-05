package ru.otus.cafe.order.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long menuItemId,
        String menuItemName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {}
