package com.SpringBoot.EventServices.services;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import com.SpringBoot.EventServices.dto.SeatingChartRequest;
import com.SpringBoot.EventServices.dto.SeatingChartResponse;

@FeignClient(name = "SeatingService")
public interface SeatingServiceClient {

    // Create or update seating chart for a specific event
    @PostMapping("/api/seating-charts/event/{eventId}")
    SeatingChartResponse saveOrUpdateLayout(@PathVariable("eventId") Long eventId,
                                            @RequestBody SeatingChartRequest request);

    // Delete seating chart by ID
    @DeleteMapping("/api/seating-charts/{id}")
    void deleteSeatingChart(@PathVariable("id") Long id);

    // Optional: fetch seating chart by ID if needed
    // @GetMapping("/api/seating-charts/{id}")
    // SeatingChartResponse getSeatingChartById(@PathVariable("id") Long id);
}
