package com.SpringBoot.SeatingService.controller;

import com.SpringBoot.SeatingService.model.Seat;
import com.SpringBoot.SeatingService.services.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
public class SeatController {

    @Autowired
    private SeatService seatService;

    // Get all seats by seating chart ID
    @GetMapping("/chart/{chartId}")
    public ResponseEntity<List<Seat>> getSeatsByChartId(@PathVariable Long chartId) {
        List<Seat> seats = seatService.getSeatsByChartId(chartId);
        return ResponseEntity.ok(seats);
    }

    // Book a seat by seat ID
    @PostMapping("/{seatId}/book")
    public ResponseEntity<String> bookSeat(@PathVariable Long seatId) {
        boolean booked = seatService.bookSeat(seatId);
        if (booked) {
            return ResponseEntity.ok("Seat booked successfully.");
        } else {
            return ResponseEntity.badRequest().body("Seat is already booked or not found.");
        }
    }

    // Check seat availability by seat ID
    @GetMapping("/{seatId}/availability")
    public ResponseEntity<String> checkAvailability(@PathVariable Long seatId) {
        boolean available = seatService.isSeatAvailable(seatId);
        if (available) {
            return ResponseEntity.ok("Seat is available.");
        } else {
            return ResponseEntity.ok("Seat is not available.");
        }
    }
}
