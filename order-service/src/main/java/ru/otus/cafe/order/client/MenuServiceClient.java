package ru.otus.cafe.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.cafe.order.dto.MenuItemInfoDto;

import java.util.List;

@FeignClient(name = "menu-service", fallback = MenuServiceClientFallback.class)
public interface MenuServiceClient {

    @GetMapping("/api/menu/items/{id}")
    MenuItemInfoDto getMenuItem(@PathVariable Long id);

    @GetMapping("/api/menu/items/batch")
    List<MenuItemInfoDto> getMenuItemsBatch(@RequestParam List<Long> ids);
}