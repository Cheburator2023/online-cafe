package ru.otus.cafe.menu.mapper;

import org.springframework.stereotype.Component;
import ru.otus.cafe.menu.dto.MenuItemResponse;
import ru.otus.cafe.menu.model.MenuItem;

@Component
public class MenuItemMapper {
    public MenuItemResponse toResponse(MenuItem menuItem) {
        return new MenuItemResponse(
                menuItem.getId(),
                menuItem.getName(),
                menuItem.getDescription(),
                menuItem.getPrice(),
                menuItem.getCategory().name(),
                menuItem.getAvailable(),
                menuItem.getCreatedAt()
        );
    }
}