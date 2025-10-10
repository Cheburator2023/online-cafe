package ru.otus.cafe.menu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.cafe.menu.model.Category;
import ru.otus.cafe.menu.model.MenuItem;

import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByCategory(Category category);
    List<MenuItem> findByAvailableTrue();
}