package com.SpringBoot.EventServices.services;

import com.SpringBoot.EventServices.dto.EventDTO;
import com.SpringBoot.EventServices.model.Event;
import com.SpringBoot.EventServices.model.Venue;
import com.SpringBoot.EventServices.repository.EventRepository;
import com.SpringBoot.EventServices.repository.VenueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceUnitTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private VenueRepository venueRepository;

    @Mock
    private SeatingServiceClient seatingServiceClient;

    @Mock
    private com.cloudinary.Cloudinary cloudinary;

    @Mock
    private EventNotificationProducer eventNotificationProducer;

    @InjectMocks
    private EventService eventService;

    private EventDTO dto;

    @BeforeEach
    void setUp() {
        dto = new EventDTO();
        dto.setName("Name");
        dto.setCategory("Cat");
        dto.setDescription("Desc");
        dto.setOrganizerId(1L);
        Venue v = new Venue();
        v.setName("V");
        dto.setVenue(new com.SpringBoot.EventServices.dto.VenueDTO());
        dto.getVenue().setName("V");
        dto.setStartDate(LocalDate.now());
        dto.setStartTime(LocalTime.now());
        dto.setEndDate(LocalDate.now());
        dto.setEndTime(LocalTime.now());
    }

    @Test
    void shouldCreateEventWithoutImage() {
        when(eventRepository.save(any(Event.class))).thenAnswer(i -> { Event e = i.getArgument(0); e.setId(5L); return e; });

        Event saved = eventService.createEvent(dto, null);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isEqualTo(5L);
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void shouldThrowWhenGetEventMissing() {
        when(eventRepository.findById(10L)).thenReturn(Optional.empty());

        try {
            eventService.getEventById(10L);
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).contains("Event not found");
        }
        verify(eventRepository).findById(10L);
    }
}
