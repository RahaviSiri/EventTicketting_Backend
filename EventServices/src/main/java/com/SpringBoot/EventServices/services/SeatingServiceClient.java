package com.SpringBoot.EventServices.services;

import org.springframework.cloud.openfeign.FeignClient;
// import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.SpringBoot.EventServices.dto.SeatingChartRequest;
import com.SpringBoot.EventServices.dto.SeatingChartResponse;

@FeignClient(name = "SeatingService")
public interface SeatingServiceClient {

    @PostMapping("/api/seating-charts")
    SeatingChartResponse createSeatingChart(@RequestBody SeatingChartRequest request);

    @PutMapping("/api/seating-charts/{id}")
    void updateSeatingChart(@PathVariable("id") Long id, @RequestBody SeatingChartRequest request); 

    @DeleteMapping("/api/seating-charts/{id}")
    void deleteSeatingChart(@PathVariable("id") Long id);

    // @GetMapping("/api/seating-charts/{eventId}")
    // ResponseEntity<String> getLayoutByEventId(@PathVariable("eventId") Long eventId);
}




