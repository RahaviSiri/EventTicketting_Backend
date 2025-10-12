package com.SpringBoot.AdminService.client;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.SpringBoot.AdminService.dto.EventAdminDto;
import com.SpringBoot.AdminService.dto.UserDTO;

@FeignClient(name = "EventServices", path = "/api/events")
public interface EventClient {

    @GetMapping("/summary")
    Map<String, Object> getEventsSummary(@RequestParam("range") String range);

    @GetMapping("/monthly-events")
    List<Map<String, Object>> getEventsLast6Months();

    @GetMapping("/all")
    Map<String, Object> getAllEventsForAdminRaw(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size);

    @GetMapping("/admin/{id}")
    Map<String, Object> getEventById(@PathVariable("id") Long id);

    @GetMapping("/active/count/{organizerId}")
    long getActiveEventsCount(@RequestParam("organizerId") Long organizerId);

    @GetMapping("/pending/count/{organizerId}")
    long getPendingEventsCount(@RequestParam("organizerId") Long organizerId);

    @PutMapping("/{eventId}/status/{status}")
    void updateEventStatus(@PathVariable("eventId") Long eventId, @PathVariable("status") String status);


}
