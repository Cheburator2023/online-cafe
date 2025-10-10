package ru.otus.cafe.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.cafe.order.model.Order;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
}