package com.event.PaymentService.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.event.PaymentService.model.Payment;

@DataJpaTest
class PaymentRepositoryTest {

    @Autowired
    PaymentRepository paymentRepository;

    @Test
    void saveAndFindByPaymentId() {
        Payment p = new Payment();
    p.setPaymentId("PAY-XYZ");
    p.setUserId(1L);
    p.setAmount(java.math.BigDecimal.valueOf(10));
    p.setCurrency("USD");
    p.setPaymentMethod("CARD");
    p.setStatus(com.event.PaymentService.model.PaymentStatus.PENDING);

        var saved = paymentRepository.save(p);
        assertThat(saved.getId()).isNotNull();

        var fetched = paymentRepository.findByPaymentId("PAY-XYZ");
        assertThat(fetched).isPresent();
    }
}
