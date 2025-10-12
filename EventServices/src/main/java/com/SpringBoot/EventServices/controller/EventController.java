package com.SpringBoot.EventServices.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.SpringBoot.EventServices.dto.EventAdminDto;
import com.SpringBoot.EventServices.dto.EventDTO;
import com.SpringBoot.EventServices.dto.EventWithDetailsDto;
import com.SpringBoot.EventServices.dto.SeatingChartRequest;
import com.SpringBoot.EventServices.model.Event;
import com.SpringBoot.EventServices.services.EventService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/events")
// @CrossOrigin(origins = "http://localhost:5173")
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping("/paged")
    public ResponseEntity<Page<Event>> getAllEvents(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> events = eventService.getAllEvents(pageable);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{eventId}")
    public Event getEventById(@PathVariable Long eventId) {
        return eventService.getEventById(eventId);
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Event> createEvent(@RequestPart("event") @Validated EventDTO eventDTO,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        Event savedEvent = eventService.createEvent(eventDTO, imageFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEvent);
    }

    @PutMapping(value = "/{eventId}", consumes = "multipart/form-data")
    public ResponseEntity<Event> updateEvent(@PathVariable Long eventId,
            @RequestPart("event") @Validated EventDTO eventDTO,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        Event updatedEvent = eventService.updateEvent(eventId, eventDTO, imageFile);
        return ResponseEntity.ok(updatedEvent);
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Map<String, String>> deleteEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.ok(Map.of("message", "Event deleted successfully"));
    }

    @GetMapping("/organizer/{organizerId}/paged")
    public ResponseEntity<Page<EventWithDetailsDto>> getEventsByOrganizerId(
            @PathVariable Long organizerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<EventWithDetailsDto> events = eventService.getEventsWithDetailsByOrganizerId(organizerId, pageable);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/organizer/{organizerId}")
    public ResponseEntity<List<EventWithDetailsDto>> getEventsByOrganizerId(@PathVariable Long organizerId) {
        List<EventWithDetailsDto> events = eventService.getEventsWithDetailsByOrganizerId(organizerId);
        return ResponseEntity.ok(events);
    }

    @PostMapping("/{eventId}/saveLayout")
    public ResponseEntity<Event> saveLayout(@PathVariable Long eventId,
            @RequestBody SeatingChartRequest request) {
        System.out.println("Received layoutJson: " + request.getLayoutJson());
        Event updatedEvent = eventService.updateSeatingLayout(eventId, request);
        return ResponseEntity.ok(updatedEvent);
    }

    // public static class LayoutRequest {
    // private String layoutJson;

    // public String getLayoutJson() { return layoutJson; }
    // public void setLayoutJson(String layoutJson) { this.layoutJson = layoutJson;
    // }
    // }

    // admin part

    // @GetMapping("/count")
    // public ResponseEntity<Map<String, Long>> getEventCount() {
    // return ResponseEntity.ok(Map.of("totalEvents",
    // eventService.getEventCount()));
    // }

    @GetMapping("/summary")
    public Map<String, Object> getEventsSummary(@RequestParam String range) {
        return eventService.getEventsSummary(range);
    }

    @GetMapping("/monthly-events")
    public List<Map<String, Object>> getEventsLast6Months() {
        return eventService.getEventsLast6Months();
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllEventsForAdminRaw(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<EventAdminDto> eventsPage = eventService.getAllEventsForAdmin(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", eventsPage.getContent());
        response.put("pageable", Map.of(
                "pageNumber", eventsPage.getNumber(),
                "pageSize", eventsPage.getSize()));
        response.put("totalElements", eventsPage.getTotalElements());
        response.put("totalPages", eventsPage.getTotalPages());
        response.put("isFirst", eventsPage.isFirst());
        response.put("isLast", eventsPage.isLast());

        return ResponseEntity.ok(response);
    }

    // get active events based on organizerId
    @GetMapping("/active/count/{organizerId}")
    public ResponseEntity<Long> getActiveEventsCount(@PathVariable Long organizerId) {
        return ResponseEntity.ok(eventService.getActiveEventsCount(organizerId));
    }

    @GetMapping("/pending/count/{organizerId}")
    public ResponseEntity<Long> getPendingEventsCount(@PathVariable Long organizerId) {
        return ResponseEntity.ok(eventService.getPendingEventsCount(organizerId));
    }

    // @GetMapping("/admin/{eventId}")
    // public ResponseEntity<EventAdminDto> getEventByIdForAdmin(@PathVariable Long eventId) {
    //     EventAdminDto event = eventService.getEventByIdForAdmin(eventId);
    //     return ResponseEntity.ok(event);
    // }

    @GetMapping("/admin/{id}")
    public ResponseEntity<Map<String, Object>> getEventByIdForAdmin(@PathVariable Long id) {
        Map<String, Object> event = eventService.getEventByIdForAdmin(id);
        if (event == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(event);
    }

    @PutMapping("/{eventId}/status/{status}")
    public ResponseEntity<String> updateEventStatus(@PathVariable Long eventId,
            @PathVariable String status) {
        eventService.updateEventStatus(eventId, status);
        return ResponseEntity.ok("Event status updated to " + status);
    }
}
