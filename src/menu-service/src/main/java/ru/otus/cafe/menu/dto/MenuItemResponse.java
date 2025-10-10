package ru.otus.cafe.menu.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record MenuItemResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        String category,
        Boolean available,
        Instant createdAt
) {}