package ru.otus.cafe.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.cafe.common.event.PaymentProcessedEvent;
import ru.otus.cafe.payment.config.RabbitMQConfig;
import ru.otus.cafe.payment.dto.PaymentRequest;
import ru.otus.cafe.payment.dto.PaymentResponse;
import ru.otus.cafe.payment.exception.PaymentNotFoundException;
import ru.otus.cafe.payment.mapper.PaymentMapper;
import ru.otus.cafe.payment.model.Payment;
import ru.otus.cafe.payment.model.PaymentStatus;
import ru.otus.cafe.payment.repository.PaymentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentGatewayService paymentGatewayService;
    private final RabbitTemplate rabbitTemplate;

    @Override
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        log.info("Processing payment request for order {} and user {}",
                request.orderId(), request.userId());

        // Проверяем, не существует ли уже платеж для этого заказа
        List<Payment> existingPayments = paymentRepository.findByOrderId(request.orderId());
        if (!existingPayments.isEmpty()) {
            log.warn("Payment already exists for order {}", request.orderId());
            throw new IllegalArgumentException("Payment already exists for order: " + request.orderId());
        }

        Payment payment = new Payment(
                request.orderId(),
                request.userId(),
                request.amount(),
                request.paymentMethod() != null ? request.paymentMethod() : "CREDIT_CARD"
        );

        // Обработка через платежный шлюз
        PaymentStatus gatewayStatus = paymentGatewayService.processPaymentThroughGateway(payment);
        payment.setStatus(gatewayStatus);

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment {} saved with status {}", savedPayment.getId(), savedPayment.getStatus());

        // Отправка события о обработке платежа
        sendPaymentProcessedEvent(savedPayment);

        return paymentMapper.toResponse(savedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long id) {
        log.debug("Fetching payment by id: {}", id);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with id: " + id));
        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponse updatePaymentStatus(Long id, String status) {
        log.info("Updating payment {} status to {}", id, status);

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with id: " + id));

        PaymentStatus newStatus;
        try {
            newStatus = PaymentStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid payment status: " + status);
        }

        // Проверка допустимости перехода статуса
        validateStatusTransition(payment.getStatus(), newStatus);

        payment.setStatus(newStatus);
        Payment updatedPayment = paymentRepository.save(payment);

        // Если статус изменился на COMPLETED, отправляем событие
        if (newStatus == PaymentStatus.COMPLETED && payment.getStatus() != newStatus) {
            sendPaymentProcessedEvent(updatedPayment);
        }

        return paymentMapper.toResponse(updatedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByUserId(Long userId) {
        log.debug("Fetching payments for user: {}", userId);
        return paymentRepository.findByUserId(userId).stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByOrderId(Long orderId) {
        log.debug("Fetching payments for order: {}", orderId);
        return paymentRepository.findByOrderId(orderId).stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByStatus(String status) {
        log.debug("Fetching payments with status: {}", status);
        PaymentStatus paymentStatus = PaymentStatus.valueOf(status.toUpperCase());
        return paymentRepository.findByStatus(paymentStatus).stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    private void sendPaymentProcessedEvent(Payment payment) {
        PaymentProcessedEvent event = new PaymentProcessedEvent(
                payment.getId(),
                payment.getOrderId(),
                payment.getUserId(),
                payment.getAmount(),
                payment.getStatus().name(),
                payment.getPaymentMethod(),
                payment.getCreatedAt()
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PAYMENT_EXCHANGE,
                "payment.processed",
                event
        );

        log.info("Sent payment processed event for payment {}", payment.getId());
    }

    private void validateStatusTransition(PaymentStatus current, PaymentStatus newStatus) {
        if (current == PaymentStatus.COMPLETED && newStatus == PaymentStatus.PENDING) {
            throw new IllegalArgumentException("Cannot revert COMPLETED payment to PENDING");
        }

        if (current == PaymentStatus.REFUNDED && newStatus != PaymentStatus.REFUNDED) {
            throw new IllegalArgumentException("Cannot change status of REFUNDED payment");
        }
    }
}