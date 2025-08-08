package com.SpringBoot.EventServices.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SpringBoot.EventServices.dto.EventDTO;
import com.SpringBoot.EventServices.dto.EventWithDetailsDto;
import com.SpringBoot.EventServices.dto.SeatingChartRequest;
import com.SpringBoot.EventServices.dto.SeatingChartResponse;
import com.SpringBoot.EventServices.model.Event;
import com.SpringBoot.EventServices.model.Venue;
import com.SpringBoot.EventServices.repository.EventRepository;
import com.SpringBoot.EventServices.repository.VenueRepository;

import jakarta.transaction.Transactional;

@Service
public class EventService {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    VenueRepository venueRepository;

    @Autowired
    private SeatingServiceClient seatingServiceClient;

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Event createEvent(EventDTO eventDTO) {
        Event event = new Event();
        event.setOrganizerId(eventDTO.getOrganizerId());

        Venue venue = new Venue();
        venue.setName(eventDTO.getVenue().getName());
        venue.setAddress(eventDTO.getVenue().getAddress());
        venue.setCity(eventDTO.getVenue().getCity());
        venue.setState(eventDTO.getVenue().getState());
        venue.setPostalCode(eventDTO.getVenue().getPostalCode());
        venue.setCountry(eventDTO.getVenue().getCountry());
        venue.setCapacity(eventDTO.getVenue().getCapacity());
        venue.setDescription(eventDTO.getVenue().getDescription());

        event.setVenue(venue);
        event.setName(eventDTO.getName());
        event.setDescription(eventDTO.getDescription());
        event.setStartDate(eventDTO.getStartDate());
        event.setStartTime(eventDTO.getStartTime());
        event.setEndDate(eventDTO.getEndDate());
        event.setEndTime(eventDTO.getEndTime());
        event.setStatus("CREATED");
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());

        // Save event first to generate event ID for seating chart
        Event savedEvent = eventRepository.save(event);

        // Generate seating layout JSON (customize as needed)
        String layoutJson = generateDefaultLayoutJson(venue.getCapacity());
        System.out.println("Generated Layout JSON: " + layoutJson);

        SeatingChartRequest seatingRequest = new SeatingChartRequest();
        seatingRequest.setEventId(savedEvent.getId());
        seatingRequest.setLayoutJson(layoutJson);

        // Call Seating Service to create seating chart
        SeatingChartResponse seatingResponse = seatingServiceClient.createSeatingChart(seatingRequest);

        // Update event with seating chart ID and save
        savedEvent.setSeatingChartId(seatingResponse.getId());
        savedEvent.setUpdatedAt(LocalDateTime.now());

        return eventRepository.save(savedEvent);
    }

    public Event updateEvent(Long eventId, EventDTO eventDTO) {
        Event existingEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId));

        existingEvent.setOrganizerId(eventDTO.getOrganizerId());

        // Update Venue
        Venue venue = existingEvent.getVenue();
        venue.setName(eventDTO.getVenue().getName());
        venue.setAddress(eventDTO.getVenue().getAddress());
        venue.setCity(eventDTO.getVenue().getCity());
        venue.setState(eventDTO.getVenue().getState());
        venue.setPostalCode(eventDTO.getVenue().getPostalCode());
        venue.setCountry(eventDTO.getVenue().getCountry());
        venue.setCapacity(eventDTO.getVenue().getCapacity());
        venue.setDescription(eventDTO.getVenue().getDescription());

        // Update Event details
        existingEvent.setName(eventDTO.getName());
        existingEvent.setDescription(eventDTO.getDescription());
        existingEvent.setStartDate(eventDTO.getStartDate());
        existingEvent.setStartTime(eventDTO.getStartTime());
        existingEvent.setEndDate(eventDTO.getEndDate());
        existingEvent.setEndTime(eventDTO.getEndTime());
        existingEvent.setStatus("UPDATED");
        existingEvent.setUpdatedAt(LocalDateTime.now());

        // Inform Seating Service if needed (if layout changed)
        if(venue.getCapacity() != null && venue.getCapacity() != existingEvent.getVenue().getCapacity()) {
            // If capacity changed, want to update the seating chart
            SeatingChartRequest seatingRequest = new SeatingChartRequest();
            seatingRequest.setEventId(existingEvent.getId());
            seatingRequest.setLayoutJson(generateDefaultLayoutJson(venue.getCapacity()));
            seatingServiceClient.updateSeatingChart(existingEvent.getSeatingChartId(),seatingRequest);

        }
        return eventRepository.save(existingEvent);
    }


    private String generateDefaultLayoutJson(Integer capacity) {
        // Your logic to generate seating layout JSON, e.g.:
        // For simplicity, returning empty JSON here.
        return """
        {
        "seats": [
            {
            "seatNumber": "A1",
            "row": "A",
            "section": "Main",
            "seatType": "VIP",
            "isAccessible": false,
            "status": "available"
            },
            {
            "seatNumber": "A2",
            "row": "A",
            "section": "Main",
            "seatType": "Regular",
            "isAccessible": true,
            "status": "available"
            }
        ]
        }
        """;
    }
    
    @Transactional
    public void deleteEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId));
        // Inform Seating Service to delete seating chart
        if (event.getSeatingChartId() != null) {
            seatingServiceClient.deleteSeatingChart(event.getSeatingChartId());
        }
        eventRepository.delete(event);
    }

    public List<EventWithDetailsDto> getEventsWithDetailsByOrganizerId(Long organizerId) {
        List<Event> events = eventRepository.findByOrganizerId(organizerId);
        List<EventWithDetailsDto> eventDetails = new ArrayList<>();

        for (Event event : events) {
            Venue venue = event.getVenue();

            EventWithDetailsDto dto = new EventWithDetailsDto();
            dto.setEvent(event);
            dto.setVenue(venue);
            dto.setSeatingChartId(event.getSeatingChartId());;

            eventDetails.add(dto);
        }

        return eventDetails;
    }
}
