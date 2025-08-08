package com.SpringBoot.SeatingService.controller;

import com.SpringBoot.SeatingService.model.SeatingChart;
import com.SpringBoot.SeatingService.services.SeatingChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seating-charts")
public class SeatingChartController {

    @Autowired
    private SeatingChartService seatingChartService;

    // Create new seating chart
    @PostMapping
    public ResponseEntity<SeatingChart> createSeatingChart(@RequestBody SeatingChart seatingChart) {
        SeatingChart created = seatingChartService.createSeatingChart(seatingChart);
        return ResponseEntity.ok(created);
    }

    // Get seating chart by ID
    @GetMapping("/{id}")
    public ResponseEntity<SeatingChart> getSeatingChartById(@PathVariable Long id) {
        SeatingChart chart = seatingChartService.getSeatingChartById(id);
        return chart != null ? ResponseEntity.ok(chart) : ResponseEntity.notFound().build();
    }

    // Update seating chart
    @PutMapping("/{id}")
    public ResponseEntity<SeatingChart> updateSeatingChart(@PathVariable Long id, @RequestBody SeatingChart updatedChart) {
        SeatingChart chart = seatingChartService.updateSeatingChart(id, updatedChart);
        return chart != null ? ResponseEntity.ok(chart) : ResponseEntity.notFound().build();
    }

    // Delete seating chart
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeatingChart(@PathVariable Long id) {
        boolean deleted = seatingChartService.deleteSeatingChart(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
