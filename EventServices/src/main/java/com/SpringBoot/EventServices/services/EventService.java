package com.SpringBoot.EventServices.services;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import com.SpringBoot.EventServices.dto.EventAdminDto;
import com.SpringBoot.EventServices.dto.EventDTO;
import com.SpringBoot.EventServices.dto.EventWithDetailsDto;
import com.SpringBoot.EventServices.dto.SeatingChartRequest;
import com.SpringBoot.EventServices.dto.SeatingChartResponse;
import com.SpringBoot.EventServices.dto.VenueDTO;
import com.SpringBoot.EventServices.model.Event;
import com.SpringBoot.EventServices.model.Venue;
import com.SpringBoot.EventServices.repository.EventRepository;
import com.SpringBoot.EventServices.repository.VenueRepository;
import com.SpringBoot.EventServices.util.DateRangeUtil;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import jakarta.transaction.Transactional;

@Service
public class EventService {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    VenueRepository venueRepository;

    @Autowired
    private SeatingServiceClient seatingServiceClient;

    @Autowired
    private Cloudinary cloudinary;


    @Autowired
    private EventNotificationProducer eventNotificationProducer;


    // public List<Event> getAllEvents() {
    //     return eventRepository.findAll();

    public Page<Event> getAllEvents(Pageable pageable) {
        Page<Event> events = eventRepository.findAll(pageable);
        return events;
    }

    public Event createEvent(EventDTO eventDTO, MultipartFile imageFile) {
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
        event.setCategory(eventDTO.getCategory());
        event.setDescription(eventDTO.getDescription());
        event.setStartDate(eventDTO.getStartDate());
        event.setStartTime(eventDTO.getStartTime());
        event.setEndDate(eventDTO.getEndDate());
        event.setEndTime(eventDTO.getEndTime());
        event.setStatus("CREATED");
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());

        // Upload image to Cloudinary
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                Map uploadResult = cloudinary.uploader().upload(imageFile.getBytes(), ObjectUtils.emptyMap());
                if (uploadResult != null && uploadResult.get("secure_url") != null) {
                    String imageUrl = uploadResult.get("secure_url").toString();
                    event.setImageUrl(imageUrl);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Save event first to generate event ID for seating chart
        event.setSeatingChartId(null);
        Event savedEvent = eventRepository.save(event);

        return savedEvent;
    }

    public Event updateSeatingLayout(Long eventId, SeatingChartRequest request) {
        // SeatingChartRequest request = new SeatingChartRequest();
        // request.setLayoutJson(layoutJson);

        // Call seating service
        SeatingChartResponse chart = seatingServiceClient.saveOrUpdateLayout(eventId, request);

        // Update Event with seatingChartId
        Event event = getEventById(eventId);
        event.setSeatingChartId(chart.getId());
        event.setUpdatedAt(LocalDateTime.now());

        return eventRepository.save(event);
    }

    public Event updateEvent(Long eventId, EventDTO eventDTO, MultipartFile imageFile) {
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
        existingEvent.setCategory(eventDTO.getCategory());
        existingEvent.setDescription(eventDTO.getDescription());
        existingEvent.setStartDate(eventDTO.getStartDate());
        existingEvent.setStartTime(eventDTO.getStartTime());
        existingEvent.setEndDate(eventDTO.getEndDate());
        existingEvent.setEndTime(eventDTO.getEndTime());
        existingEvent.setStatus("UPDATED");
        existingEvent.setUpdatedAt(LocalDateTime.now());

        // Handle image update if new image uploaded
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                Map uploadResult = cloudinary.uploader().upload(imageFile.getBytes(), ObjectUtils.emptyMap());
                if (uploadResult != null && uploadResult.get("secure_url") != null) {
                    String imageUrl = uploadResult.get("secure_url").toString();
                    existingEvent.setImageUrl(imageUrl);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Event saved = eventRepository.save(existingEvent);

        // Publish event-updated message for NotificationService to inform ticket holders
        try {
            String payload = String.format("{\"eventId\": %d, \"note\": \"The event '%s' has been updated. Please check new details.\"}",
                    saved.getId(), saved.getName());
            eventNotificationProducer.publishEventUpdated(payload);
            System.out.println("Published event-updated for eventId=" + saved.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return saved;
    }

    @Transactional
    public void deleteEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId));
        // Inform Seating Service to delete seating chart
        if (event.getSeatingChartId() != null) {
            System.out.println("Deleting seating chart with ID: " + event.getSeatingChartId());
            seatingServiceClient.deleteSeatingChart(event.getSeatingChartId());
        }
        eventRepository.delete(event);
    }

    public Page<EventWithDetailsDto> getEventsWithDetailsByOrganizerId(Long organizerId, Pageable pageable) {
        Page<Event> events = eventRepository.findByOrganizerId(organizerId, pageable);

        List<EventWithDetailsDto> dtoList = events.getContent()
                .stream()
                .map(event -> {
                    Venue venue = event.getVenue();
                    EventWithDetailsDto dto = new EventWithDetailsDto();
                    dto.setEvent(event);
                    dto.setVenue(venue);
                    dto.setSeatingChartId(event.getSeatingChartId());
                    return dto;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, events.getTotalElements());
    }

    public Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId));
    }

    public List<EventWithDetailsDto> getEventsWithDetailsByOrganizerId(Long organizerId) {
        List<Event> events = eventRepository.findByOrganizerId(organizerId);
        List<EventWithDetailsDto> eventDetails = new ArrayList<>();

        for (Event event : events) {
            Venue venue = event.getVenue();

            EventWithDetailsDto dto = new EventWithDetailsDto();
            dto.setEvent(event);
            dto.setVenue(venue);
            dto.setSeatingChartId(event.getSeatingChartId());
            ;

            eventDetails.add(dto);
        }

        return eventDetails;
    }

    // admin part

    public Map<String, Object> getEventsSummary(String range) {
        LocalDateTime fromDate = DateRangeUtil.resolveFrom(range);
        LocalDateTime toDate = LocalDateTime.now();

        int total = eventRepository.countActiveEventsBetween(fromDate, toDate);

        // Count previous period for trend calculation
        long days = java.time.Duration.between(fromDate, toDate).toDays();
        LocalDateTime previousFrom = fromDate.minusDays(days);
        LocalDateTime previousTo = fromDate;
        int previous = eventRepository.countActiveEventsBetween(previousFrom, previousTo);

        double trend = previous == 0 ? 0 : ((double) (total - previous) / previous) * 100;

        return Map.of(
                "total", total,
                "trend", Math.round(trend * 100.0) / 100.0);
    }

    public List<Map<String, Object>> getEventsLast6Months() {
        List<Map<String, Object>> result = new ArrayList<>();
        LocalDate now = LocalDate.now();

        for (int i = 5; i >= 0; i--) {
            YearMonth month = YearMonth.from(now.minusMonths(i));
            int events = eventRepository.countEventsByMonth(month.getYear(), month.getMonthValue());

            result.add(Map.of(
                    "month", month.getMonth().name().substring(0, 3),
                    "events", events));
        }
        return result;
    }

    public Page<EventAdminDto> getAllEventsForAdmin(Pageable pageable) {
        return eventRepository.findAll(pageable)
                .map(EventAdminDto::new);
    }

    public long getActiveEventsCount(Long organizerId) {
        return eventRepository.countByOrganizerIdAndStatus(organizerId, "ACTIVE");
    }

    public long getPendingEventsCount(Long organizerId) {
        return eventRepository.countByOrganizerIdAndStatus(organizerId, "PENDING");
    }

    public Map<String, Object> getEventByIdForAdmin(Long id) {
        Optional<Event> optionalEvent = eventRepository.findById(id);
        if (optionalEvent.isEmpty()) {
            return null;
        }

        Event event = optionalEvent.get();
        Map<String, Object> eventMap = new HashMap<>();

        eventMap.put("id", event.getId());
        eventMap.put("eventName", event.getName());
        eventMap.put("description", event.getDescription());
        eventMap.put("venueName", event.getVenue() != null ? event.getVenue().getName() : null);
        eventMap.put("category", event.getCategory());
        eventMap.put("startDate", event.getStartDate());
        eventMap.put("startTime", event.getStartTime());
        eventMap.put("endDate", event.getEndDate());
        eventMap.put("endTime", event.getEndTime());
        eventMap.put("status", event.getStatus());
        eventMap.put("createdAt", event.getCreatedAt());
        eventMap.put("organizerId", event.getOrganizerId());

        return eventMap;
    }

    public void updateEventStatus(Long eventId, String status) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        event.setStatus(status.toUpperCase());
        eventRepository.save(event);
    }

}
