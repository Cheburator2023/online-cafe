package ru.otus.cafe.common.event;

import java.math.BigDecimal;

public record OrderItemEvent(
        Long menuItemId,
        String menuItemName,
        Integer quantity,
        BigDecimal unitPrice
) {}
