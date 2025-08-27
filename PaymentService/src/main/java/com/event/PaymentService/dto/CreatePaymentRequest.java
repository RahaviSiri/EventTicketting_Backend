package com.event.PaymentService.dto;

import java.math.BigDecimal;

public class CreatePaymentRequest {
    
    private Long ticketId;
    private Long userId;
    private BigDecimal amount;
    private String currency;
    private String paymentMethod;
    private String description;
    
    // Constructors
    public CreatePaymentRequest() {}
    
    public CreatePaymentRequest(Long ticketId, Long userId, BigDecimal amount, 
                               String currency, String paymentMethod, String description) {
        this.ticketId = ticketId;
        this.userId = userId;
        this.amount = amount;
        this.currency = currency;
        this.paymentMethod = paymentMethod;
        this.description = description;
    }
    
    // Getters and Setters
    public Long getTicketId() {
        return ticketId;
    }
    
    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}
