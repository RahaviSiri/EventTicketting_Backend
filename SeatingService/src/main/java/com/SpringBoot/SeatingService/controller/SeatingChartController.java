package com.SpringBoot.SeatingService.controller;

import com.SpringBoot.SeatingService.dto.SeatingChartRequest;
import com.SpringBoot.SeatingService.dto.SeatingChartResponse;
import com.SpringBoot.SeatingService.model.SeatingChart;
import com.SpringBoot.SeatingService.dto.SeatNumbersRequest;
import com.SpringBoot.SeatingService.services.SeatingChartService;
import com.SpringBoot.SeatingService.dto.SeatBatchResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    // Endpoints accept seat numbers in the request body as either a JSON array (e.g. ["A1","A2"]) or a
    // comma-separated string (e.g. "A1,A2"). Controller will parse and call service per-seat, then
    // return an aggregated result.
    @PostMapping("/{eventId}/reserve")
public ResponseEntity<Map<String, Object>> reserveSeat(
        @PathVariable Long eventId,
        @RequestBody(required = false) SeatNumbersRequest req) {
    List<String> seats = req != null && req.getSeatNumbers() != null
        ? req.getSeatNumbers()
        : new ArrayList<>();

    SeatBatchResult batchResult = seatingChartService.reserveSeats(eventId, seats);

    Map<String, Object> response = new HashMap<>();
    response.put("eventId", eventId);
    response.put("requested", seats);
    response.put("success", batchResult.isSuccess());
    response.put("unavailableSeats", batchResult.getUnavailableSeats());

    return batchResult.isSuccess()
        ? ResponseEntity.ok(response)
        : ResponseEntity.badRequest().body(response);
}


@PostMapping("/{eventId}/confirm")
public ResponseEntity<Map<String, Object>> confirmSeat(
        @PathVariable Long eventId,
        @RequestBody(required = false) SeatNumbersRequest req) {

    Map<String, Object> response = new HashMap<>();
    response.put("eventId", eventId);

    if (req == null || req.getSeatNumbers() == null || req.getSeatNumbers().isEmpty()) {
        response.put("success", false);
        response.put("results", Collections.emptyList());
        return ResponseEntity.badRequest().body(response);
    }

    List<Map<String, String>> results = new ArrayList<>();
    boolean allSuccess = true;

    for (String seat : req.getSeatNumbers()) {
        seat = seat.trim();
        if (seat.isEmpty()) continue;

        boolean confirmed = seatingChartService.confirmSeat(eventId, seat);
        Map<String, String> seatResult = new HashMap<>();
        seatResult.put("seatNumber", seat);
        seatResult.put("status", confirmed ? "booked" : "failed");

        results.add(seatResult);

        if (!confirmed) allSuccess = false;
    }

    response.put("results", results);
    response.put("success", allSuccess);

    return allSuccess ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
}


    @PostMapping("/{eventId}/release")
    public ResponseEntity<String> releaseSeat(@PathVariable Long eventId, @RequestBody(required = false) SeatNumbersRequest req) {
        String[] seats = req != null && req.getSeatNumbers() != null
                ? req.getSeatNumbers().stream().map(String::valueOf).toArray(String[]::new)
                : new String[0];
        StringBuilder result = new StringBuilder();
        boolean allSuccess = true;
        for (String seat : seats) {
            seat = seat.trim();
            if (seat.isEmpty()) continue;
            boolean released = seatingChartService.releaseSeat(eventId, seat);
            if (released) {
                result.append(seat).append(":released;");
            } else {
                result.append(seat).append(":failed;");
                allSuccess = false;
            }
        }
        String body = result.toString();
        return allSuccess ? ResponseEntity.ok(body) : ResponseEntity.badRequest().body(body);
    }

    // Helper to accept either JSON array string or comma-separated simple string
    private String[] parseSeatNumbers(String payload) {
        if (payload == null) return new String[0];
        payload = payload.trim();
        // If payload starts with [ assume JSON array of strings
        if (payload.startsWith("[")) {
            try {
                // crude parse: remove brackets and quotes then split by comma
                String inner = payload.substring(1, Math.max(1, payload.length() - 1)).replaceAll("\"", "");
                if (inner.trim().isEmpty()) return new String[0];
                return inner.split(",");
            } catch (Exception e) {
                return new String[0];
            }
        }
        // Otherwise treat as comma separated values
        if (payload.contains(",")) return payload.split(",");
        // Single seat string
        return new String[]{payload};
    }
}
