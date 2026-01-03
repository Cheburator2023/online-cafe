package ru.otus.cafe.payment.service;

import ru.otus.cafe.payment.model.Payment;
import ru.otus.cafe.payment.model.PaymentStatus;

public interface PaymentGateway {
    PaymentStatus processPayment(Payment payment);
    boolean processRefund(Payment payment);
}
