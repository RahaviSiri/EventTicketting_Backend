package com.event.TicketService.repository;

import com.event.TicketService.model.Ticket;
import com.event.TicketService.model.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    
    // Find tickets by user ID
    List<Ticket> findByUserId(Long userId);
    
    // Find tickets by event ID
    List<Ticket> findByEventId(Long eventId);
    
    // Find tickets by status
    List<Ticket> findByStatus(TicketStatus status);
    
    // Find tickets by user ID and status
    List<Ticket> findByUserIdAndStatus(Long userId, TicketStatus status);
    
    // Find tickets by event ID and status
    List<Ticket> findByEventIdAndStatus(Long eventId, TicketStatus status);
    
    // Find ticket by ticket number
    Optional<Ticket> findByTicketNumber(String ticketNumber);
    
    // Find tickets by user ID and event ID
    List<Ticket> findByUserIdAndEventId(Long userId, Long eventId);
    
    // Find tickets by seat number and event ID
    Optional<Ticket> findBySeatNumberAndEventId(String seatNumber, Long eventId);
    
    // Find tickets by purchase date range
    @Query("SELECT t FROM Ticket t WHERE t.purchaseDate BETWEEN :startDate AND :endDate")
    List<Ticket> findByPurchaseDateBetween(@Param("startDate") LocalDateTime startDate, 
                                          @Param("endDate") LocalDateTime endDate);
    
    // Find tickets by event date range
    @Query("SELECT t FROM Ticket t WHERE t.eventDate BETWEEN :startDate AND :endDate")
    List<Ticket> findByEventDateBetween(@Param("startDate") LocalDateTime startDate, 
                                       @Param("endDate") LocalDateTime endDate);
    
    // Count tickets by event ID and status
    long countByEventIdAndStatus(Long eventId, TicketStatus status);
    
    // Find tickets that are expired (event date is in the past)
    @Query("SELECT t FROM Ticket t WHERE t.eventDate < :currentDate AND t.status = 'CONFIRMED'")
    List<Ticket> findExpiredTickets(@Param("currentDate") LocalDateTime currentDate);
}
