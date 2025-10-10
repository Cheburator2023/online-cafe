package ru.otus.cafe.menu.service;

import ru.otus.cafe.menu.dto.MenuItemRequest;
import ru.otus.cafe.menu.dto.MenuItemResponse;

import java.util.List;

public interface MenuService {
    MenuItemResponse createMenuItem(MenuItemRequest request);
    MenuItemResponse getMenuItemById(Long id);
    MenuItemResponse updateMenuItem(Long id, MenuItemRequest request);
    void deleteMenuItem(Long id);
    List<MenuItemResponse> getAllMenuItems();
    List<MenuItemResponse> getMenuItemsByCategory(String category);
}