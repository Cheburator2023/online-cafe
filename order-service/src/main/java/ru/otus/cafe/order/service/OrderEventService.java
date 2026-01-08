package ru.otus.cafe.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import ru.otus.cafe.order.messaging.OrderEventPublisher;
import ru.otus.cafe.order.model.Order;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEventService {

    private final OrderEventPublisher orderEventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCreated(OrderCreatedEvent event) {
        try {
            orderEventPublisher.publishOrderCreatedEvent(event.order());
        } catch (Exception e) {
            log.error("Failed to publish order created event for order: {}", event.order().getId(), e);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderStatusUpdated(OrderStatusUpdatedEvent event) {
        try {
            orderEventPublisher.publishOrderStatusUpdatedEvent(event.order(), event.oldStatus());
        } catch (Exception e) {
            log.error("Failed to publish order status updated event for order: {}", event.order().getId(), e);
        }
    }

    public record OrderCreatedEvent(Order order) {}
    public record OrderStatusUpdatedEvent(Order order, String oldStatus) {}
}