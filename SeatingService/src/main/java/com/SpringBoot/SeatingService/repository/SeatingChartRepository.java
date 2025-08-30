package com.SpringBoot.SeatingService.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.SpringBoot.SeatingService.model.SeatingChart;
;

@Repository
public interface SeatingChartRepository extends JpaRepository<SeatingChart, Long> {
    Optional<SeatingChart> findByEventId(Long eventId);
}
