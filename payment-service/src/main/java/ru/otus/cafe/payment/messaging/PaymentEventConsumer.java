package ru.otus.cafe.payment.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.otus.cafe.common.event.OrderCreatedEvent;
import ru.otus.cafe.payment.config.RabbitMQConfig;
import ru.otus.cafe.payment.dto.PaymentRequest;
import ru.otus.cafe.payment.service.PaymentService;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventConsumer {
    private final PaymentService paymentService;

    @RabbitListener(queues = RabbitMQConfig.ORDER_CREATED_QUEUE)
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("Received order created event for order ID: {}", event.orderId());

        try {
            PaymentRequest paymentRequest = createPaymentRequestFromEvent(event);
            paymentService.processPayment(paymentRequest);
            log.info("Auto-created payment for order ID: {}", event.orderId());
        } catch (Exception e) {
            log.error("Failed to process payment for order ID: {}", event.orderId(), e);
            throw e; // Позволит RabbitMQ обработать ошибку через DLQ
        }
    }

    private PaymentRequest createPaymentRequestFromEvent(OrderCreatedEvent event) {
        return new PaymentRequest(
                event.orderId(),
                event.userId(),
                event.totalAmount(),
                "AUTO_CREATED"
        );
    }
}