package com.SpringBoot.SeatingService.services;

// import com.SpringBoot.SeatingService.model.Seat;
import com.SpringBoot.SeatingService.model.SeatingChart;
// import com.SpringBoot.SeatingService.repository.SeatRepository;
import com.SpringBoot.SeatingService.repository.SeatingChartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
// import java.util.List;
import java.util.Optional;

@Service
public class SeatingChartService {

    @Autowired
    private SeatingChartRepository seatingChartRepository;

    // @Autowired
    // private SeatRepository seatRepository;

    // Create or update seating chart
    public SeatingChart saveOrUpdateLayout(Long eventId, String layoutJson) {
        Optional<SeatingChart> existingChartOpt = seatingChartRepository.findByEventId(eventId);
        System.out.println("Layout JSON received: " + layoutJson);
        SeatingChart chart;
        if (existingChartOpt.isPresent()) {
            chart = existingChartOpt.get();
            chart.setLayoutJson(layoutJson);
        } else {
            chart = new SeatingChart();
            chart.setEventId(eventId);
            chart.setLayoutJson(layoutJson);
            chart.setCreatedAt(LocalDateTime.now());
        }
        return seatingChartRepository.save(chart);
    }

    // Get by ID
    public SeatingChart getSeatingChartById(Long id) {
        return seatingChartRepository.findById(id).orElse(null);
    }

    // Delete
    @Transactional
    public boolean deleteSeatingChart(Long id) {
        if (seatingChartRepository.existsById(id)) {
            // Delete all seats associated with this seating chart
            // List<Seat> seats = seatRepository.findBySeatingChartId(id);
            // seatRepository.deleteAll(seats);
            
            // Now delete the seating chart
            seatingChartRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Get by Event ID
    public SeatingChart getSeatingChartByEventId(Long id) {
        return seatingChartRepository.findByEventId(id).orElse(null);
    }
}
