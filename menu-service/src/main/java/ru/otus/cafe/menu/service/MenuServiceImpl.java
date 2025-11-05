package ru.otus.cafe.menu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.cafe.menu.dto.MenuItemRequest;
import ru.otus.cafe.menu.dto.MenuItemResponse;
import ru.otus.cafe.menu.exception.MenuItemNotFoundException;
import ru.otus.cafe.menu.mapper.MenuItemMapper;
import ru.otus.cafe.menu.model.Category;
import ru.otus.cafe.menu.model.MenuItem;
import ru.otus.cafe.menu.repository.MenuItemRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {
    private final MenuItemRepository menuItemRepository;
    private final MenuItemMapper menuItemMapper;

    @Override
    @Transactional
    public MenuItemResponse createMenuItem(MenuItemRequest request) {
        MenuItem menuItem = new MenuItem(
                request.name(),
                request.description(),
                request.price(),
                Category.valueOf(request.category().toUpperCase())
        );
        MenuItem savedItem = menuItemRepository.save(menuItem);
        return menuItemMapper.toResponse(savedItem);
    }

    @Override
    @Transactional(readOnly = true)
    public MenuItemResponse getMenuItemById(Long id) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new MenuItemNotFoundException("Menu item not found with id: " + id));
        return menuItemMapper.toResponse(menuItem);
    }

    @Override
    @Transactional
    public MenuItemResponse updateMenuItem(Long id, MenuItemRequest request) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new MenuItemNotFoundException("Menu item not found with id: " + id));

        menuItem.setName(request.name());
        menuItem.setDescription(request.description());
        menuItem.setPrice(request.price());
        menuItem.setCategory(Category.valueOf(request.category().toUpperCase()));

        MenuItem updatedItem = menuItemRepository.save(menuItem);
        return menuItemMapper.toResponse(updatedItem);
    }

    @Override
    @Transactional
    public void deleteMenuItem(Long id) {
        if (!menuItemRepository.existsById(id)) {
            throw new MenuItemNotFoundException("Menu item not found with id: " + id);
        }
        menuItemRepository.deleteById(id);
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
        Category categoryEnum = Category.valueOf(category.toUpperCase());
        return menuItemRepository.findByCategory(categoryEnum).stream()
                .map(menuItemMapper::toResponse)
                .toList();
    }
}