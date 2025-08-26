package com.SpringBoot.EventServices.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.SpringBoot.EventServices.dto.EventDTO;
import com.SpringBoot.EventServices.dto.EventWithDetailsDto;
import com.SpringBoot.EventServices.model.Event;
import com.SpringBoot.EventServices.services.EventService;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/events")
// @CrossOrigin(origins = "http://localhost:5173")
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
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

    @GetMapping("/organizer/{organizerId}")
    public ResponseEntity<List<EventWithDetailsDto>> getEventsByOrganizerId(@PathVariable Long organizerId) {
        List<EventWithDetailsDto> eventDetails = eventService.getEventsWithDetailsByOrganizerId(organizerId);
        return ResponseEntity.ok(eventDetails);
    }
}

