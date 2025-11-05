package ru.otus.cafe.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.cafe.order.dto.CreateOrderRequest;
import ru.otus.cafe.order.dto.OrderResponse;
import ru.otus.cafe.order.exception.OrderNotFoundException;
import ru.otus.cafe.order.mapper.OrderMapper;
import ru.otus.cafe.order.messaging.OrderEventPublisher;
import ru.otus.cafe.order.model.Order;
import ru.otus.cafe.order.model.OrderItem;
import ru.otus.cafe.order.model.OrderStatus;
import ru.otus.cafe.order.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderEventPublisher orderEventPublisher;

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        Order order = new Order(request.userId(), request.specialInstructions());

        // В реальном приложении здесь был бы вызов MenuService для получения информации о товарах
        request.items().forEach(item -> {
            // Заглушка - в реальном приложении нужно получать данные из меню
            OrderItem orderItem = new OrderItem(
                    item.menuItemId(),
                    "Menu Item " + item.menuItemId(), // Заглушка
                    item.quantity(),
                    BigDecimal.valueOf(10.0) // Заглушка
            );
            order.addItem(orderItem);
        });

        Order savedOrder = orderRepository.save(order);

        orderEventPublisher.publishOrderCreatedEvent(savedOrder);

        return orderMapper.toResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));
        orderEventPublisher.publishOrderCreatedEvent(order);

        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));

        order.setStatus(OrderStatus.valueOf(status.toUpperCase()));
        Order updatedOrder = orderRepository.save(order);
        orderEventPublisher.publishOrderCreatedEvent(updatedOrder);
        return orderMapper.toResponse(updatedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toResponse)
                .toList();
    }
}