package ru.otus.cafe.payment.service;

import ru.otus.cafe.payment.dto.PaymentRequest;
import ru.otus.cafe.payment.dto.PaymentResponse;

import java.util.List;

public interface PaymentService {
    PaymentResponse processPayment(PaymentRequest request);
    PaymentResponse getPaymentById(Long id);
    PaymentResponse updatePaymentStatus(Long id, String status);
    List<PaymentResponse> getPaymentsByUserId(Long userId);
    List<PaymentResponse> getPaymentsByOrderId(Long orderId);
}