package com.SpringBoot.SeatingService.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.SpringBoot.SeatingService.model.SeatingChart;

import jakarta.persistence.LockModeType;

@Repository
public interface SeatingChartRepository extends JpaRepository<SeatingChart, Long> {
    Optional<SeatingChart> findByEventId(Long eventId);

    // Acquire a pessimistic write lock on the seating chart row to prevent concurrent modifications
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM SeatingChart s WHERE s.eventId = :eventId")
    Optional<SeatingChart> findByEventIdForUpdate(@Param("eventId") Long eventId);
}
