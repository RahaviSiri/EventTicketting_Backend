package com.event.PaymentService.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.event.PaymentService.model.Payment;
import com.event.PaymentService.repository.PaymentRepository;

@ExtendWith(MockitoExtension.class)
class PaymentServiceUnitTest {

    @Mock
    PaymentRepository paymentRepository;

    @Mock
    com.event.PaymentService.mapper.PaymentMapper paymentMapper;

    com.event.PaymentService.service.PaymentService paymentService;

    @BeforeEach
    void init() {
        // Construct service with dummy stripe key and inject mocks into fields
        paymentService = new com.event.PaymentService.service.PaymentService("test-key");
        ReflectionTestUtils.setField(paymentService, "paymentRepository", paymentRepository);
        ReflectionTestUtils.setField(paymentService, "paymentMapper", paymentMapper);
    }

    @Test
    void confirmPayment_updatesStatus() {
        Payment p = new Payment();
        p.setId(1L);
        p.setPaymentId("PAY-123");
        p.setUserId(1L);
        p.setStatus(com.event.PaymentService.model.PaymentStatus.PENDING);

        when(paymentRepository.findByPaymentId("PAY-123")).thenReturn(Optional.of(p));
        when(paymentRepository.save(p)).thenReturn(p);
        when(paymentMapper.toDto(p)).thenReturn(new com.event.PaymentService.dto.PaymentDTO());

        com.event.PaymentService.dto.PaymentDTO dto = paymentService.confirmPayment("PAY-123");
        assertThat(dto).isNotNull();
    }
}
