package ru.otus.cafe.order.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.otus.cafe.order.dto.MenuItemInfoDto;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
public class MenuServiceClientFallback implements MenuServiceClient {

    @Override
    public MenuItemInfoDto getMenuItem(Long id) {
        log.warn("Fallback triggered for menu item with id: {}", id);
        return new MenuItemInfoDto(id, "Menu Item " + id, BigDecimal.valueOf(10.0));
    }

    @Override
    public List<MenuItemInfoDto> getMenuItemsBatch(List<Long> ids) {
        log.warn("Fallback triggered for menu items batch: {}", ids);
        return ids.stream()
                .map(id -> new MenuItemInfoDto(id, "Menu Item " + id, BigDecimal.valueOf(10.0)))
                .toList();
    }
}