package com.event.TicketService.controller;

import com.event.TicketService.dto.CreateTicketRequest;
import com.event.TicketService.dto.TicketDTO;
import org.springframework.test.context.ActiveProfiles;
import com.event.TicketService.service.TicketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TicketController.class)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TicketService ticketService;

    private CreateTicketRequest request;

    @BeforeEach
    void setUp() {
        request = new CreateTicketRequest();
        request.setEventId(1L);
        request.setUserId(2L);
        request.setSeatNumbers("A1");
        request.setPrice(10.0);
        request.setEventDate(LocalDateTime.now().plusDays(1));
        request.setVenueName("V");
        request.setEventName("E");
    }

    @Test
    @WithMockUser
    void shouldCreateTicketAndReturnQr() throws Exception {
        TicketDTO dto = TicketDTO.builder().id(100L).qrCode("qr").build();
        when(ticketService.createTicket(any(CreateTicketRequest.class))).thenReturn(dto);

    mockMvc.perform(post("/api/tickets").with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ticketId").value(100))
                .andExpect(jsonPath("$.qrCode").value("qr"));

        verify(ticketService, times(1)).createTicket(any());
    }

    @Test
    @WithMockUser
    void shouldReturnTicketById() throws Exception {
        TicketDTO dto = TicketDTO.builder().id(5L).eventId(1L).userId(2L).build();
        when(ticketService.getTicketById(5L)).thenReturn(dto);

        mockMvc.perform(get("/api/tickets/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5));

        verify(ticketService).getTicketById(5L);
    }

    @Test
    @WithMockUser
    void shouldReturnNotFoundWhenGetTicketMissing() throws Exception {
        when(ticketService.getTicketById(9L)).thenThrow(new RuntimeException("not found"));

        mockMvc.perform(get("/api/tickets/9"))
                .andExpect(status().isNotFound());

        verify(ticketService).getTicketById(9L);
    }

}
