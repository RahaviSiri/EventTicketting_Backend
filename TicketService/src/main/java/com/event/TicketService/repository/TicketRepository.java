package com.event.TicketService.repository;

import com.event.TicketService.model.Ticket;
import com.event.TicketService.model.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

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

        // Find tickets by user ID and event ID
        List<Ticket> findByUserIdAndEventId(Long userId, Long eventId);

        // Find tickets by purchase date range
        @Query("SELECT t FROM Ticket t WHERE t.purchaseDate BETWEEN :startDate AND :endDate")
        List<Ticket> findByPurchaseDateBetween(@Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        // Find tickets by event date range
        @Query("SELECT t FROM Ticket t WHERE t.eventDate BETWEEN :startDate AND :endDate")
        List<Ticket> findByEventDateBetween(@Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        // Find total Revenue Sum of Organizer for all event
        @Query(value = "SELECT COALESCE(SUM(t.price),0) " +
                        "FROM tickets t " +
                        "WHERE t.event_id IN (:eventIds)", nativeQuery = true)
        Double findRevenueByEventIds(@Param("eventIds") List<Long> eventIds);

        // Find Tickets count eventID
        @Query(value = "SELECT COALESCE(COUNT(*), 0) " +
                        "FROM tickets t " +
                        "WHERE t.event_id IN (:eventIds)", nativeQuery = true)
        Long countTicketsByEventIds(@Param("eventIds") List<Long> eventIds);

        // Sum Revenue for a event
        @Query(value = "SELECT COALESCE(SUM(t.price),0) FROM tickets t WHERE t.event_id = :eventId", nativeQuery = true)
        Double findRevenueByEventId(@Param("eventId") Long eventId);

        // Count tickets for a single event
        @Query(value = "SELECT COUNT(t) FROM tickets t WHERE t.event_id = :eventId", nativeQuery = true)
        Long countTicketsByEvent(@Param("eventId") Long eventId);

}


// COALESCE = "Give me the first value that isn’t null, or a default if all are null."