package ru.otus.cafe.payment.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.cafe.payment.model.Payment;
import ru.otus.cafe.payment.model.PaymentStatus;

@Service
@Slf4j
public class PaymentGatewayService implements PaymentGateway {

    /**
     * Имитация вызова внешнего платежного шлюза
     */
    @Override
    public PaymentStatus processPayment(Payment payment) {
        log.info("Processing payment {} through external gateway for order {}",
                payment.getId(), payment.getOrderId());

        validatePaymentAmount(payment);

        if (isRandomGatewayFailure()) {
            log.error("Random gateway failure for payment {}", payment.getId());
            return PaymentStatus.FAILED;
        }

        return PaymentStatus.COMPLETED;
    }

    /**
     * Имитация возврата платежа
     */
    @Override
    public boolean processRefund(Payment payment) {
        log.info("Processing refund for payment {}", payment.getId());
        return payment.getStatus() == PaymentStatus.COMPLETED;
    }

    /**
     * Для обратной совместимости
     */
    public PaymentStatus processPaymentThroughGateway(Payment payment) {
        return processPayment(payment);
    }

    private void validatePaymentAmount(Payment payment) {
        if (payment.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            log.warn("Invalid amount for payment {}: {}", payment.getId(), payment.getAmount());
            throw new IllegalArgumentException("Payment amount must be positive");
        }

        if (payment.getAmount().compareTo(java.math.BigDecimal.valueOf(10000)) > 0) {
            log.warn("Amount too high for payment {}: {}", payment.getId(), payment.getAmount());
            throw new IllegalArgumentException("Payment amount exceeds maximum limit");
        }
    }

    private boolean isRandomGatewayFailure() {
        return Math.random() < 0.05; // 5% вероятность сбоя (только для тестов)
    }
}