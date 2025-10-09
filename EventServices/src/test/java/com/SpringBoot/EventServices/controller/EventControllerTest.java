package com.SpringBoot.EventServices.controller;

import com.SpringBoot.EventServices.services.EventService;
import com.SpringBoot.EventServices.dto.EventDTO;
import com.SpringBoot.EventServices.dto.VenueDTO;
import com.SpringBoot.EventServices.dto.EventWithDetailsDto;
import com.SpringBoot.EventServices.model.Event;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EventController.class)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EventService eventService;

    private EventDTO dto;

    @BeforeEach
    void setUp() {
        dto = new EventDTO();
        dto.setName("E");
        dto.setVenue(new VenueDTO());
        dto.getVenue().setName("V");
    }

    @Test
    void shouldReturnPagedEvents() throws Exception {
        when(eventService.getAllEvents(any())).thenReturn(org.springframework.data.domain.Page.empty());

        mockMvc.perform(get("/api/events/paged"))
                .andExpect(status().isOk());
    }

}
