package com.event.PaymentService.controller;

import com.event.PaymentService.dto.CreatePaymentRequest;
import com.event.PaymentService.dto.PaymentDTO;
import com.event.PaymentService.dto.PaymentResponse;
import com.event.PaymentService.model.PaymentStatus;
import com.event.PaymentService.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {
    
    @Autowired
    private PaymentService paymentService;
    
    // Create a new payment
    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody CreatePaymentRequest request) {
        try {
            PaymentResponse response = paymentService.createPayment(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    // Confirm payment
    @PutMapping("/{paymentId}/confirm")
    public ResponseEntity<PaymentDTO> confirmPayment(@PathVariable String paymentId) {
        try {
            PaymentDTO payment = paymentService.confirmPayment(paymentId);
            return new ResponseEntity<>(payment, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    // Stripe webhook endpoint
    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody Map<String, Object> payload) {
        try {
            // Extract payment intent ID and status from webhook payload
            Map<String, Object> data = (Map<String, Object>) payload.get("data");
            Map<String, Object> object = (Map<String, Object>) data.get("object");
            String paymentIntentId = (String) object.get("id");
            String status = (String) object.get("status");
            
            // Process the webhook
            paymentService.processPaymentWebhook(paymentIntentId, status);
            
            return new ResponseEntity<>("Webhook processed successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Webhook processing failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    // Get payment by ID
    @GetMapping("/{id}")
    public ResponseEntity<PaymentDTO> getPaymentById(@PathVariable Long id) {
        try {
            PaymentDTO payment = paymentService.getPaymentById(id);
            return new ResponseEntity<>(payment, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    // Get payment by payment ID
    @GetMapping("/payment-id/{paymentId}")
    public ResponseEntity<PaymentDTO> getPaymentByPaymentId(@PathVariable String paymentId) {
        try {
            PaymentDTO payment = paymentService.getPaymentByPaymentId(paymentId);
            return new ResponseEntity<>(payment, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    // Get all payments by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByUserId(@PathVariable Long userId) {
        try {
            List<PaymentDTO> payments = paymentService.getPaymentsByUserId(userId);
            return new ResponseEntity<>(payments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Get all payments by ticket ID
    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByTicketId(@PathVariable Long ticketId) {
        try {
            List<PaymentDTO> payments = paymentService.getPaymentsByTicketId(ticketId);
            return new ResponseEntity<>(payments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Update payment status
    @PutMapping("/{id}/status")
    public ResponseEntity<PaymentDTO> updatePaymentStatus(
            @PathVariable Long id, 
            @RequestParam PaymentStatus status) {
        try {
            PaymentDTO payment = paymentService.updatePaymentStatus(id, status);
            return new ResponseEntity<>(payment, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    // Cancel payment
    @PutMapping("/{paymentId}/cancel")
    public ResponseEntity<PaymentDTO> cancelPayment(@PathVariable String paymentId) {
        try {
            PaymentDTO payment = paymentService.cancelPayment(paymentId);
            return new ResponseEntity<>(payment, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    // Refund payment
    @PutMapping("/{paymentId}/refund")
    public ResponseEntity<PaymentDTO> refundPayment(@PathVariable String paymentId) {
        try {
            PaymentDTO payment = paymentService.refundPayment(paymentId);
            return new ResponseEntity<>(payment, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    // Get payments by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByStatus(@PathVariable PaymentStatus status) {
        try {
            List<PaymentDTO> payments = paymentService.getPaymentsByStatus(status);
            return new ResponseEntity<>(payments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Get payments by user ID and status
    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByUserIdAndStatus(
            @PathVariable Long userId, 
            @PathVariable PaymentStatus status) {
        try {
            List<PaymentDTO> payments = paymentService.getPaymentsByUserIdAndStatus(userId, status);
            return new ResponseEntity<>(payments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Get failed payments
    @GetMapping("/failed")
    public ResponseEntity<List<PaymentDTO>> getFailedPayments() {
        try {
            List<PaymentDTO> payments = paymentService.getFailedPayments();
            return new ResponseEntity<>(payments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Get pending payments
    @GetMapping("/pending")
    public ResponseEntity<List<PaymentDTO>> getPendingPayments() {
        try {
            List<PaymentDTO> payments = paymentService.getPendingPayments();
            return new ResponseEntity<>(payments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Count payments by status
    @GetMapping("/status/{status}/count")
    public ResponseEntity<Long> countPaymentsByStatus(@PathVariable PaymentStatus status) {
        try {
            long count = paymentService.countPaymentsByStatus(status);
            return new ResponseEntity<>(count, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Count payments by user ID and status
    @GetMapping("/user/{userId}/status/{status}/count")
    public ResponseEntity<Long> countPaymentsByUserIdAndStatus(
            @PathVariable Long userId, 
            @PathVariable PaymentStatus status) {
        try {
            long count = paymentService.countPaymentsByUserIdAndStatus(userId, status);
            return new ResponseEntity<>(count, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
