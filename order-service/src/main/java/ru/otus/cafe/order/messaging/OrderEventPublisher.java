package ru.otus.cafe.order.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import ru.otus.cafe.common.event.OrderCreatedEvent;
import ru.otus.cafe.common.event.OrderStatusUpdatedEvent;
import ru.otus.cafe.order.model.Order;
import ru.otus.cafe.order.model.OrderItem;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public void publishOrderCreatedEvent(Order order) {
        OrderCreatedEvent event = new OrderCreatedEvent(
                order.getId(),
                order.getUserId(),
                order.getTotalAmount(),
                order.getItems().stream()
                        .map(this::toOrderItemEvent)
                        .collect(Collectors.toList()),
                order.getCreatedAt()
        );

        rabbitTemplate.convertAndSend("order.exchange", "order.created", event);
        log.info("Published order created event for order ID: {}", order.getId());
    }

    public void publishOrderStatusUpdatedEvent(Order order, String oldStatus) {
        OrderStatusUpdatedEvent event = new OrderStatusUpdatedEvent(
                order.getId(),
                order.getUserId(),
                order.getStatus().name(),
                oldStatus,
                order.getUpdatedAt() != null ? order.getUpdatedAt() : order.getCreatedAt()
        );

        rabbitTemplate.convertAndSend("order.exchange", "order.status.updated", event);
        log.info("Published order status updated event for order ID: {}", order.getId());
    }

    private ru.otus.cafe.common.event.OrderItemEvent toOrderItemEvent(OrderItem item) {
        return new ru.otus.cafe.common.event.OrderItemEvent(
                item.getMenuItemId(),
                item.getMenuItemName(),
                item.getQuantity(),
                item.getUnitPrice()
        );
    }
}