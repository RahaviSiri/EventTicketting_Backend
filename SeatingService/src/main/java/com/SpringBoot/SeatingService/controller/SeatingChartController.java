package com.SpringBoot.SeatingService.controller;

import com.SpringBoot.SeatingService.dto.SeatingChartRequest;
import com.SpringBoot.SeatingService.dto.SeatingChartResponse;
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

    // Create or update seating chart for an event
    @PostMapping("/event/{eventId}")
    public ResponseEntity<SeatingChartResponse> saveOrUpdateLayout(
            @PathVariable Long eventId,
            @RequestBody SeatingChartRequest request) {

        SeatingChart chart = seatingChartService.saveOrUpdateLayout(eventId, request.getLayoutJson());
        SeatingChartResponse response = new SeatingChartResponse();
        response.setId(chart.getId());
        response.setEventId(chart.getEventId());
        response.setLayoutJson(chart.getLayoutJson());
        response.setCreatedAt(chart.getCreatedAt());

        return ResponseEntity.ok(response);
    }

    // Get seating chart by ID
    @GetMapping("/{id}")
    public ResponseEntity<SeatingChart> getSeatingChartById(@PathVariable Long id) {
        SeatingChart chart = seatingChartService.getSeatingChartById(id);
        return chart != null ? ResponseEntity.ok(chart) : ResponseEntity.notFound().build();
    }

    // Delete seating chart by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeatingChart(@PathVariable Long id) {
        boolean deleted = seatingChartService.deleteSeatingChart(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // Get seating chart by event ID
    @GetMapping("event/{eventID}")
    public ResponseEntity<SeatingChartResponse> getSeatingChartByEventId(@PathVariable Long eventID) {
        SeatingChart chart = seatingChartService.getSeatingChartByEventId(eventID);
        if (chart == null) return ResponseEntity.notFound().build();

        SeatingChartResponse response = new SeatingChartResponse();
        response.setId(chart.getId());
        response.setEventId(chart.getEventId());
        response.setLayoutJson(chart.getLayoutJson());
        response.setCreatedAt(chart.getCreatedAt());

        return ResponseEntity.ok(response);
    }


    @PostMapping("/{eventId}/reserve/{seatNumber}")
    public ResponseEntity<String> reserveSeat(@PathVariable Long eventId, @PathVariable String seatNumber) {
        System.out.println("Reserving seat " + seatNumber + " for event " + eventId);
        boolean reserved = seatingChartService.reserveSeat(eventId, seatNumber);
        return reserved ? ResponseEntity.ok("Seat reserved") : ResponseEntity.badRequest().body("Seat not available");
    }

    @PostMapping("/{eventId}/confirm/{seatNumber}")
    public ResponseEntity<String> confirmSeat(@PathVariable Long eventId, @PathVariable String seatNumber) {
        boolean confirmed = seatingChartService.confirmSeat(eventId, seatNumber);
        return confirmed ? ResponseEntity.ok("Seat booked") : ResponseEntity.badRequest().body("Seat not reserved");
    }

    @PostMapping("/{eventId}/release/{seatNumber}")
    public ResponseEntity<String> releaseSeat(@PathVariable Long eventId, @PathVariable String seatNumber) {
        boolean released = seatingChartService.releaseSeat(eventId, seatNumber);
        return released ? ResponseEntity.ok("Seat released") : ResponseEntity.badRequest().body("Seat not reserved");
    }
}
