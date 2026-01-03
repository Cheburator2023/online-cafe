package ru.otus.cafe.menu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import ru.otus.cafe.menu.model.Category;

import java.math.BigDecimal;

public record MenuItemRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Description is required")
        String description,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be positive")
        BigDecimal price,

        @NotBlank(message = "Category is required")
        @Pattern(
                regexp = "^(?i)(COFFEE|TEA|DESSERT|SANDWICH|SALAD)$",
                message = "Invalid category. Allowed values: COFFEE, TEA, DESSERT, SANDWICH, SALAD"
        )
        String category
) {
    public Category getCategoryAsEnum() {
        return Category.valueOf(category.toUpperCase());
    }
}