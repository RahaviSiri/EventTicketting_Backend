package com.event.PaymentService.dto;

import com.event.PaymentService.model.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {

    private Long id;
    private String paymentId;
    private Long ticketId;
    private Long userId;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus status;
    private String paymentMethod;
    private String stripePaymentIntentId;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String receiptUrl;
}
