package com.SpringBoot.EventServices.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.SpringBoot.EventServices.model.Event;

import feign.Param;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
       Page<Event> findByOrganizerId(Long organizerId, Pageable pageable);

       // It Runs Query SELECT * FROM events e
       // WHERE e.organizer_id = :organizerId
       // LIMIT 6 OFFSET 12;
       List<Event> findByOrganizerId(Long organizerId);

       // admin part

       @Query("SELECT COUNT(e) " +
                     "FROM Event e " +
                     "WHERE YEAR(e.createdAt) = :year " +
                     "AND MONTH(e.createdAt) = :month")
       int countEventsByMonth(@Param("year") int year, @Param("month") int month);

       @Query("SELECT COUNT(e) " +
                     "FROM Event e " +
                     "WHERE e.status = 'ACTIVE' " +
                     "AND e.createdAt  >= :from AND e.createdAt  < :to")
       int countActiveEventsBetween(@Param("from") LocalDateTime from,
                     @Param("to") LocalDateTime to);

       @Query("SELECT COUNT(e) FROM Event e WHERE e.organizerId = :organizerId AND e.status = :status")
       long countByOrganizerIdAndStatus(@Param("organizerId") Long organizerId, @Param("status") String status);

}
