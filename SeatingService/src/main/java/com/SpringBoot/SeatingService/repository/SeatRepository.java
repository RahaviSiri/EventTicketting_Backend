package com.SpringBoot.SeatingService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.SpringBoot.SeatingService.model.Seat;
import java.util.List;


@Repository
public interface SeatRepository extends JpaRepository<Seat,Long> {
    List<Seat> findBySeatingChartId(Long seatingChartId);
} 