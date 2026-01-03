package ru.otus.cafe.payment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.cafe.payment.dto.PaymentRequest;
import ru.otus.cafe.payment.exception.PaymentNotFoundException;
import ru.otus.cafe.payment.mapper.PaymentMapper;
import ru.otus.cafe.payment.model.Payment;
import ru.otus.cafe.payment.model.PaymentStatus;
import ru.otus.cafe.payment.repository.PaymentRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private PaymentGatewayService paymentGatewayService;

    private PaymentServiceImpl paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentServiceImpl(
                paymentRepository,
                paymentMapper,
                paymentGatewayService,
                null // RabbitTemplate не нужен для тестов
        );
    }

    @Test
    void getPaymentById_WhenPaymentExists_ShouldReturnPayment() {
        // Arrange
        Long paymentId = 1L;
        Payment payment = new Payment(1L, 1L, BigDecimal.valueOf(100), "CREDIT_CARD");
        payment.setStatus(PaymentStatus.COMPLETED);

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        // Act & Assert
        assertDoesNotThrow(() -> paymentService.getPaymentById(paymentId));
        verify(paymentRepository).findById(paymentId);
    }

    @Test
    void getPaymentById_WhenPaymentNotFound_ShouldThrowException() {
        // Arrange
        Long paymentId = 999L;
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PaymentNotFoundException.class,
                () -> paymentService.getPaymentById(paymentId));
    }

    @Test
    void updatePaymentStatus_WithInvalidStatus_ShouldThrowException() {
        // Arrange
        Long paymentId = 1L;
        Payment payment = new Payment(1L, 1L, BigDecimal.valueOf(100), "CREDIT_CARD");
        payment.setStatus(PaymentStatus.COMPLETED);

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> paymentService.updatePaymentStatus(paymentId, "INVALID_STATUS"));
    }

    @Test
    void updatePaymentStatus_FromCompletedToPending_ShouldThrowException() {
        // Arrange
        Long paymentId = 1L;
        Payment payment = new Payment(1L, 1L, BigDecimal.valueOf(100), "CREDIT_CARD");
        payment.setStatus(PaymentStatus.COMPLETED);

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> paymentService.updatePaymentStatus(paymentId, "PENDING"));
    }
}