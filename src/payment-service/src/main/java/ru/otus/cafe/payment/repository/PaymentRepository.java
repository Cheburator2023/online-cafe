package ru.otus.cafe.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.cafe.payment.model.Payment;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUserId(Long userId);
    List<Payment> findByOrderId(Long orderId);
    List<Payment> findByStatus(String status);
}