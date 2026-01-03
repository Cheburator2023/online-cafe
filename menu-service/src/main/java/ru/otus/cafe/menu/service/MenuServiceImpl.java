package ru.otus.cafe.menu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.cafe.menu.dto.MenuItemRequest;
import ru.otus.cafe.menu.dto.MenuItemResponse;
import ru.otus.cafe.menu.exception.InvalidCategoryException;
import ru.otus.cafe.menu.exception.MenuItemNotFoundException;
import ru.otus.cafe.menu.mapper.MenuItemMapper;
import ru.otus.cafe.menu.model.Category;
import ru.otus.cafe.menu.model.MenuItem;
import ru.otus.cafe.menu.repository.MenuItemRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {
    private final MenuItemRepository menuItemRepository;
    private final MenuItemMapper menuItemMapper;

    @Override
    @Transactional
    public MenuItemResponse createMenuItem(MenuItemRequest request) {
        try {
            MenuItem menuItem = new MenuItem(
                    request.name(),
                    request.description(),
                    request.price(),
                    request.getCategoryAsEnum()
            );
            MenuItem savedItem = menuItemRepository.save(menuItem);
            log.info("Created menu item with id: {}", savedItem.getId());
            return menuItemMapper.toResponse(savedItem);
        } catch (IllegalArgumentException e) {
            log.error("Invalid category value: {}", request.category());
            throw new InvalidCategoryException(request.category());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public MenuItemResponse getMenuItemById(Long id) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Menu item not found with id: {}", id);
                    return new MenuItemNotFoundException("Menu item not found with id: " + id);
                });
        return menuItemMapper.toResponse(menuItem);
    }

    @Override
    @Transactional
    public MenuItemResponse updateMenuItem(Long id, MenuItemRequest request) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Menu item not found with id: {}", id);
                    return new MenuItemNotFoundException("Menu item not found with id: " + id);
                });

        try {
            menuItem.setName(request.name());
            menuItem.setDescription(request.description());
            menuItem.setPrice(request.price());
            menuItem.setCategory(request.getCategoryAsEnum());
        } catch (IllegalArgumentException e) {
            log.error("Invalid category value during update: {}", request.category());
            throw new InvalidCategoryException(request.category());
        }

        MenuItem updatedItem = menuItemRepository.save(menuItem);
        log.info("Updated menu item with id: {}", id);
        return menuItemMapper.toResponse(updatedItem);
    }

    @Override
    @Transactional
    public void deleteMenuItem(Long id) {
        if (!menuItemRepository.existsById(id)) {
            log.warn("Menu item not found with id: {}", id);
            throw new MenuItemNotFoundException("Menu item not found with id: " + id);
        }
        menuItemRepository.deleteById(id);
        log.info("Deleted menu item with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemResponse> getAllMenuItems() {
        return menuItemRepository.findAll().stream()
                .map(menuItemMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemResponse> getMenuItemsByCategory(String category) {
        try {
            Category categoryEnum = Category.valueOf(category.toUpperCase());
            return menuItemRepository.findByCategory(categoryEnum).stream()
                    .map(menuItemMapper::toResponse)
                    .toList();
        } catch (IllegalArgumentException e) {
            log.error("Invalid category value in query: {}", category);
            throw new InvalidCategoryException(category);
        }
    }
}