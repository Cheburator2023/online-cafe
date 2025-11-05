package ru.otus.cafe.payment.mapper;

import org.springframework.stereotype.Component;
import ru.otus.cafe.payment.dto.PaymentResponse;
import ru.otus.cafe.payment.model.Payment;

@Component
public class PaymentMapper {
    public PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrderId(),
                payment.getUserId(),
                payment.getAmount(),
                payment.getStatus().name(),
                payment.getPaymentMethod(),
                payment.getCreatedAt()
        );
    }
}