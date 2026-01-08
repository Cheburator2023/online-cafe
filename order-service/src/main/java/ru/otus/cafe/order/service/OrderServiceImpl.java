package ru.otus.cafe.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.cafe.order.dto.CreateOrderRequest;
import ru.otus.cafe.order.dto.OrderResponse;
import ru.otus.cafe.order.exception.OrderNotFoundException;
import ru.otus.cafe.order.factory.OrderFactory;
import ru.otus.cafe.order.mapper.OrderMapper;
import ru.otus.cafe.order.model.Order;
import ru.otus.cafe.order.model.OrderStatus;
import ru.otus.cafe.order.repository.OrderRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderFactory orderFactory;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        log.info("Creating order for user: {}", request.userId());

        Order order = orderFactory.createOrder(request);
        Order savedOrder = orderRepository.save(order);

        eventPublisher.publishEvent(new OrderEventService.OrderCreatedEvent(savedOrder));

        log.info("Order created successfully with id: {}", savedOrder.getId());
        return orderMapper.toResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        log.debug("Getting order by id: {}", id);
        Order order = findOrderById(id);
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long id, String status) {
        log.info("Updating order status for id: {}, new status: {}", id, status);

        Order order = findOrderById(id);
        String oldStatus = order.getStatus().name();

        try {
            OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase());
            order.setStatus(newStatus);
            Order updatedOrder = orderRepository.save(order);

            eventPublisher.publishEvent(new OrderEventService.OrderStatusUpdatedEvent(updatedOrder, oldStatus));

            log.info("Order status updated successfully for id: {}", id);
            return orderMapper.toResponse(updatedOrder);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid order status: " + status);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUserId(Long userId) {
        log.debug("Getting orders for user: {}", userId);
        return orderRepository.findByUserId(userId).stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        log.debug("Getting all orders with pagination");
        return orderRepository.findAll(pageable)
                .map(orderMapper::toResponse);
    }

    private Order findOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));
    }
}