package ru.otus.cafe.order.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.otus.cafe.order.dto.CreateOrderRequest;
import ru.otus.cafe.order.dto.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(CreateOrderRequest request);
    OrderResponse getOrderById(Long id);
    OrderResponse updateOrderStatus(Long id, String status);
    List<OrderResponse> getOrdersByUserId(Long userId);
    Page<OrderResponse> getAllOrders(Pageable pageable);
}