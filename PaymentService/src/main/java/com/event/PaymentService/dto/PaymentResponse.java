package com.event.PaymentService.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private String paymentId;
    private String stripePaymentIntentId;
    private String clientSecret;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String message;
}
