package com.SpringBoot.EventServices.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SpringBoot.EventServices.dto.EventDTO;
import com.SpringBoot.EventServices.dto.EventWithDetailsDto;
import com.SpringBoot.EventServices.model.Event;
import com.SpringBoot.EventServices.services.EventService;

import java.util.List;
import java.util.Map;

import org.apache.hc.core5.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    EventService eventService;

    @GetMapping
    public List<Event> getAllEvents() {
        List<Event> events = eventService.getAllEvents();
        return events;
    }

    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody EventDTO eventDTO) {
        Event savedEvent = eventService.createEvent(eventDTO);
        return ResponseEntity.status(HttpStatus.SC_CREATED).body(savedEvent);
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long eventId, @RequestBody EventDTO eventDTO) {
        return ResponseEntity.ok(eventService.updateEvent(eventId, eventDTO));
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.ok().body(Map.of("message", "Event deleted successfully"));
    }

    @GetMapping("/organizer/{organizerId}")
    public ResponseEntity<List<EventWithDetailsDto>> getEventsByOrganizerId(@PathVariable Long organizerId) {
        List<EventWithDetailsDto> eventDetails = eventService.getEventsWithDetailsByOrganizerId(organizerId);
        return ResponseEntity.ok(eventDetails);
    }
}
