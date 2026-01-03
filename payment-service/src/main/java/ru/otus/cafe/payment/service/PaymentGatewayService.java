package ru.otus.cafe.payment.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.cafe.payment.model.Payment;
import ru.otus.cafe.payment.model.PaymentStatus;

import java.math.BigDecimal;

@Service
@Slf4j
public class PaymentGatewayService {

    /**
     * Имитация вызова внешнего платежного шлюза
     */
    public PaymentStatus processPaymentThroughGateway(Payment payment) {
        log.info("Processing payment {} through external gateway for order {}",
                payment.getId(), payment.getOrderId());

        // Имитация логики платежного шлюза
        // В реальном приложении здесь был бы вызов API платежной системы

        // Пример простой бизнес-логики:
        if (payment.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Invalid amount for payment {}: {}", payment.getId(), payment.getAmount());
            return PaymentStatus.FAILED;
        }

        if (payment.getAmount().compareTo(BigDecimal.valueOf(10000)) > 0) {
            log.warn("Amount too high for payment {}: {}", payment.getId(), payment.getAmount());
            return PaymentStatus.FAILED;
        }

        // Имитация случайных сбоев для реалистичности (в продакшене убрать)
        if (Math.random() < 0.05) { // 5% вероятность сбоя
            log.error("Random gateway failure for payment {}", payment.getId());
            return PaymentStatus.FAILED;
        }

        return PaymentStatus.COMPLETED;
    }

    /**
     * Имитация возврата платежа
     */
    public boolean processRefund(Payment payment) {
        log.info("Processing refund for payment {}", payment.getId());
        // В реальном приложении здесь был бы вызов API для возврата
        return payment.getStatus() == PaymentStatus.COMPLETED;
    }
}