package ru.otus.cafe.order.service;

import ru.otus.cafe.order.dto.CreateOrderRequest;
import ru.otus.cafe.order.dto.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(CreateOrderRequest request);
    OrderResponse getOrderById(Long id);
    OrderResponse updateOrderStatus(Long id, String status);
    List<OrderResponse> getOrdersByUserId(Long userId);
    List<OrderResponse> getAllOrders();
}