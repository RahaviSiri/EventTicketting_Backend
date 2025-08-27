package com.event.PaymentService.service;

import com.event.PaymentService.dto.CreatePaymentRequest;
import com.event.PaymentService.dto.PaymentDTO;
import com.event.PaymentService.dto.PaymentResponse;
import com.event.PaymentService.model.Payment;
import com.event.PaymentService.model.PaymentStatus;
import com.event.PaymentService.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Value("${stripe.secret.key}")
    private String stripeSecretKey;
    
    // Initialize Stripe
    public PaymentService(@Value("${stripe.secret.key}") String stripeSecretKey) {
        Stripe.apiKey = stripeSecretKey;
    }
    
    // Create a new payment
    public PaymentResponse createPayment(CreatePaymentRequest request) {
        try {
            // Generate unique payment ID
            String paymentId = generatePaymentId();
            
            // Create payment record
            Payment payment = new Payment();
            payment.setPaymentId(paymentId);
            payment.setTicketId(request.getTicketId());
            payment.setUserId(request.getUserId());
            payment.setAmount(request.getAmount());
            payment.setCurrency(request.getCurrency());
            payment.setStatus(PaymentStatus.PENDING);
            payment.setPaymentMethod(request.getPaymentMethod());
            payment.setDescription(request.getDescription());
            
            // Save payment
            Payment savedPayment = paymentRepository.save(payment);
            
            // Create Stripe payment intent
            PaymentIntent paymentIntent = createStripePaymentIntent(request);
            
            // Update payment with Stripe payment intent ID
            savedPayment.setStripePaymentIntentId(paymentIntent.getId());
            paymentRepository.save(savedPayment);
            
            return new PaymentResponse(
                paymentId,
                paymentIntent.getId(),
                paymentIntent.getClientSecret(),
                request.getAmount(),
                request.getCurrency(),
                "PENDING",
                "Payment created successfully"
            );
            
        } catch (Exception e) {
            throw new RuntimeException("Error creating payment: " + e.getMessage());
        }
    }
    
    // Create Stripe payment intent
    private PaymentIntent createStripePaymentIntent(CreatePaymentRequest request) throws StripeException {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
            .setAmount(convertToStripeAmount(request.getAmount(), request.getCurrency()))
            .setCurrency(request.getCurrency().toLowerCase())
            .setDescription(request.getDescription())
            .putMetadata("ticketId", request.getTicketId().toString())
            .putMetadata("userId", request.getUserId().toString())
            .build();
        
        return PaymentIntent.create(params);
    }
    
    // Convert amount to Stripe format (cents for USD)
    private Long convertToStripeAmount(BigDecimal amount, String currency) {
        if ("USD".equalsIgnoreCase(currency) || "EUR".equalsIgnoreCase(currency)) {
            return amount.multiply(BigDecimal.valueOf(100)).longValue();
        }
        return amount.longValue();
    }
    
    // Confirm payment
    public PaymentDTO confirmPayment(String paymentId) {
        Optional<Payment> paymentOpt = paymentRepository.findByPaymentId(paymentId);
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setReceiptUrl("https://receipt.stripe.com/" + payment.getStripePaymentIntentId());
            Payment updatedPayment = paymentRepository.save(payment);
            return convertToDTO(updatedPayment);
        }
        throw new RuntimeException("Payment not found with id: " + paymentId);
    }
    
    // Process payment webhook from Stripe
    public void processPaymentWebhook(String paymentIntentId, String status) {
        Optional<Payment> paymentOpt = paymentRepository.findByStripePaymentIntentId(paymentIntentId);
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            
            switch (status.toLowerCase()) {
                case "succeeded":
                    payment.setStatus(PaymentStatus.COMPLETED);
                    payment.setReceiptUrl("https://receipt.stripe.com/" + paymentIntentId);
                    break;
                case "processing":
                    payment.setStatus(PaymentStatus.PROCESSING);
                    break;
                case "requires_payment_method":
                case "requires_confirmation":
                case "requires_action":
                    payment.setStatus(PaymentStatus.PENDING);
                    break;
                case "canceled":
                    payment.setStatus(PaymentStatus.CANCELLED);
                    break;
                case "failed":
                    payment.setStatus(PaymentStatus.FAILED);
                    payment.setFailureReason("Payment failed on Stripe");
                    break;
                default:
                    payment.setStatus(PaymentStatus.PENDING);
            }
            
            paymentRepository.save(payment);
        }
    }
    
    // Get payment by ID
    public PaymentDTO getPaymentById(Long id) {
        Optional<Payment> payment = paymentRepository.findById(id);
        if (payment.isPresent()) {
            return convertToDTO(payment.get());
        }
        throw new RuntimeException("Payment not found with id: " + id);
    }
    
    // Get payment by payment ID
    public PaymentDTO getPaymentByPaymentId(String paymentId) {
        Optional<Payment> payment = paymentRepository.findByPaymentId(paymentId);
        if (payment.isPresent()) {
            return convertToDTO(payment.get());
        }
        throw new RuntimeException("Payment not found with payment id: " + paymentId);
    }
    
    // Get all payments by user ID
    public List<PaymentDTO> getPaymentsByUserId(Long userId) {
        List<Payment> payments = paymentRepository.findByUserId(userId);
        return payments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // Get all payments by ticket ID
    public List<PaymentDTO> getPaymentsByTicketId(Long ticketId) {
        List<Payment> payments = paymentRepository.findByTicketId(ticketId);
        return payments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // Update payment status
    public PaymentDTO updatePaymentStatus(Long paymentId, PaymentStatus newStatus) {
        Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            payment.setStatus(newStatus);
            Payment updatedPayment = paymentRepository.save(payment);
            return convertToDTO(updatedPayment);
        }
        throw new RuntimeException("Payment not found with id: " + paymentId);
    }
    
    // Cancel payment
    public PaymentDTO cancelPayment(String paymentId) {
        Optional<Payment> paymentOpt = paymentRepository.findByPaymentId(paymentId);
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            payment.setStatus(PaymentStatus.CANCELLED);
            Payment updatedPayment = paymentRepository.save(payment);
            return convertToDTO(updatedPayment);
        }
        throw new RuntimeException("Payment not found with payment id: " + paymentId);
    }
    
    // Refund payment
    public PaymentDTO refundPayment(String paymentId) {
        Optional<Payment> paymentOpt = paymentRepository.findByPaymentId(paymentId);
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            payment.setStatus(PaymentStatus.REFUNDED);
            Payment updatedPayment = paymentRepository.save(payment);
            return convertToDTO(updatedPayment);
        }
        throw new RuntimeException("Payment not found with payment id: " + paymentId);
    }
    
    // Get payments by status
    public List<PaymentDTO> getPaymentsByStatus(PaymentStatus status) {
        List<Payment> payments = paymentRepository.findByStatus(status);
        return payments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // Get payments by user ID and status
    public List<PaymentDTO> getPaymentsByUserIdAndStatus(Long userId, PaymentStatus status) {
        List<Payment> payments = paymentRepository.findByUserIdAndStatus(userId, status);
        return payments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // Get failed payments
    public List<PaymentDTO> getFailedPayments() {
        List<Payment> payments = paymentRepository.findFailedPayments();
        return payments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // Get pending payments
    public List<PaymentDTO> getPendingPayments() {
        List<Payment> payments = paymentRepository.findPendingPayments();
        return payments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // Count payments by status
    public long countPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.countByStatus(status);
    }
    
    // Count payments by user ID and status
    public long countPaymentsByUserIdAndStatus(Long userId, PaymentStatus status) {
        return paymentRepository.countByUserIdAndStatus(userId, status);
    }
    
    // Generate unique payment ID
    private String generatePaymentId() {
        return "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    // Convert Payment entity to DTO
    private PaymentDTO convertToDTO(Payment payment) {
        return new PaymentDTO(
            payment.getId(),
            payment.getPaymentId(),
            payment.getTicketId(),
            payment.getUserId(),
            payment.getAmount(),
            payment.getCurrency(),
            payment.getStatus(),
            payment.getPaymentMethod(),
            payment.getStripePaymentIntentId(),
            payment.getDescription(),
            payment.getCreatedAt(),
            payment.getUpdatedAt(),
            payment.getFailureReason(),
            payment.getReceiptUrl()
        );
    }
}
