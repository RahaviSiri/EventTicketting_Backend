package com.event.PaymentService.repository;

import com.event.PaymentService.model.Payment;
import com.event.PaymentService.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    // Find payment by payment ID
    Optional<Payment> findByPaymentId(String paymentId);
    
    // Find payments by user ID
    List<Payment> findByUserId(Long userId);
    
    // Find payments by ticket ID
    List<Payment> findByTicketId(Long ticketId);
    
    // Find payments by status
    List<Payment> findByStatus(PaymentStatus status);
    
    // Find payments by user ID and status
    List<Payment> findByUserIdAndStatus(Long userId, PaymentStatus status);
    
    // Find payments by ticket ID and status
    List<Payment> findByTicketIdAndStatus(Long ticketId, PaymentStatus status);
    
    // Find payment by Stripe payment intent ID
    Optional<Payment> findByStripePaymentIntentId(String stripePaymentIntentId);
    
    // Find payments by amount range
    @Query("SELECT p FROM Payment p WHERE p.amount BETWEEN :minAmount AND :maxAmount")
    List<Payment> findByAmountBetween(@Param("minAmount") BigDecimal minAmount, 
                                     @Param("maxAmount") BigDecimal maxAmount);
    
    // Find payments by creation date range
    @Query("SELECT p FROM Payment p WHERE p.createdAt BETWEEN :startDate AND :endDate")
    List<Payment> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                        @Param("endDate") LocalDateTime endDate);
    
    // Find payments by payment method
    List<Payment> findByPaymentMethod(String paymentMethod);
    
    // Count payments by status
    long countByStatus(PaymentStatus status);
    
    // Count payments by user ID and status
    long countByUserIdAndStatus(Long userId, PaymentStatus status);
    
    // Find failed payments
    @Query("SELECT p FROM Payment p WHERE p.status = 'FAILED'")
    List<Payment> findFailedPayments();
    
    // Find pending payments
    @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING'")
    List<Payment> findPendingPayments();
    
    // Find completed payments by date range
    @Query("SELECT p FROM Payment p WHERE p.status = 'COMPLETED' AND p.createdAt BETWEEN :startDate AND :endDate")
    List<Payment> findCompletedPaymentsByDateRange(@Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate);
                                                   
    // for admin part

    // monthly revenue
    @Query("SELECT COALESCE(SUM(p.amount), 0) " +
            "FROM Payment p " +
            "WHERE YEAR(p.createdAt) = :year " +
            "AND MONTH(p.createdAt) = :month")
    long sumRevenueByMonth(@Param("year") int year, @Param("month") int month);

    // for calculate trend 
    @Query("SELECT COALESCE(SUM(p.amount), 0) " +
            "FROM Payment p " +
            "WHERE p.createdAt >= :from AND p.createdAt < :to")
    long sumRevenueBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
