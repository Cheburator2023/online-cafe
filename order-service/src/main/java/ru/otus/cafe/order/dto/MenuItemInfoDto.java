package ru.otus.cafe.order.dto;

import java.math.BigDecimal;

public record MenuItemInfoDto(
        Long id,
        String name,
        BigDecimal price
) {}