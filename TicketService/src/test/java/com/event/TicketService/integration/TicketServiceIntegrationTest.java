package com.event.TicketService.integration;

import com.event.TicketService.dto.CreateTicketRequest;
import com.event.TicketService.model.Ticket;
import com.event.TicketService.model.TicketStatus;
import com.event.TicketService.repository.TicketRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TicketServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TicketRepository ticketRepository;

    @Test
    void shouldCreateTicketEndToEnd() throws Exception {
        CreateTicketRequest req = new CreateTicketRequest();
        req.setEventId(11L);
        req.setUserId(12L);
        req.setSeatNumbers("C1");
        req.setPrice(15.0);
        req.setEventDate(LocalDateTime.now().plusDays(2));
        req.setVenueName("VenueX");
        req.setEventName("EventX");

        mockMvc.perform(post("/api/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        // verify repository persisted
        Ticket saved = ticketRepository.findByUserId(12L).stream().findFirst().orElse(null);
        assertThat(saved).isNotNull();
        assertThat(saved.getEventId()).isEqualTo(11L);
        assertThat(saved.getStatus()).isEqualTo(TicketStatus.CONFIRMED);
    }
}
