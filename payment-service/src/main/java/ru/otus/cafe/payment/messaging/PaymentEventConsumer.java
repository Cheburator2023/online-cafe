package ru.otus.cafe.payment.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.otus.cafe.common.event.OrderCreatedEvent;
import ru.otus.cafe.payment.service.PaymentService;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventConsumer {
    private final PaymentService paymentService;

    @RabbitListener(queues = "order.created.queue")
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("Received order created event for order ID: {}", event.orderId());

        ru.otus.cafe.payment.dto.PaymentRequest paymentRequest = new ru.otus.cafe.payment.dto.PaymentRequest(
                event.orderId(),
                event.userId(),
                event.totalAmount(),
                "AUTO_CREATED"
        );

        paymentService.processPayment(paymentRequest);
        log.info("Auto-created payment for order ID: {}", event.orderId());
    }
}