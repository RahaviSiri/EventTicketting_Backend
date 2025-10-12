package com.SpringBoot.AdminService.client;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "EventServices")
public interface EventClient {

    @GetMapping("/api/events/summary")
    Map<String, Object> getEventsSummary(@RequestParam("range") String range);

    @GetMapping("/api/events/monthly-events")
    List<Map<String, Object>> getEventsLast6Months();

    @GetMapping("/api/events/all")
    Map<String, Object> getAllEventsForAdminRaw(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size);

    @GetMapping("/api/events/admin/{id}")
    Map<String, Object> getEventById(@PathVariable("id") Long id);

    @GetMapping("/api/events/active/count")
    long getActiveEventsCount(@RequestParam("organizerId") Long organizerId);

    @GetMapping("/api/events/pending/count")
    long getPendingEventsCount(@RequestParam("organizerId") Long organizerId);

    @PutMapping("/api/events/{eventId}/status/{status}")
    void updateEventStatus(@PathVariable("eventId") Long eventId, @PathVariable("status") String status);
}
