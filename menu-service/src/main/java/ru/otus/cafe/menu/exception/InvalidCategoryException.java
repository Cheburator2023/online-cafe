package ru.otus.cafe.menu.exception;

import ru.otus.cafe.menu.model.Category;

public class InvalidCategoryException extends RuntimeException {
    public InvalidCategoryException(String category) {
        super("Invalid category: " + category + ". Allowed values: " +
                String.join(", ", java.util.Arrays.stream(Category.values())
                        .map(Enum::name)
                        .toArray(String[]::new)));
    }
}