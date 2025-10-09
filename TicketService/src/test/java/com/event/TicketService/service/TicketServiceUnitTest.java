package com.event.TicketService.service;

import com.event.TicketService.dto.CreateTicketRequest;
import com.event.TicketService.dto.TicketDTO;
import com.event.TicketService.model.Ticket;
import com.event.TicketService.model.TicketStatus;
import com.event.TicketService.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceUnitTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private QRCodeService qrCodeService;

    @InjectMocks
    private TicketService ticketService;

    private CreateTicketRequest request;

    @BeforeEach
    void setUp() {
        request = new CreateTicketRequest();
        request.setEventId(10L);
        request.setUserId(20L);
        request.setSeatNumbers("A1,A2");
        request.setPrice(50.0);
        request.setEventDate(LocalDateTime.now().plusDays(1));
        request.setVenueName("Test Venue");
        request.setEventName("Test Event");
    }

    @Test
    void shouldSaveTicketSuccessfully() {
        when(qrCodeService.generateQRCode(any())).thenReturn("base64-qr");
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> {
            Ticket t = invocation.getArgument(0);
            t.setId(1L);
            return t;
        });

        TicketDTO result = ticketService.createTicket(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getQrCode()).isEqualTo("base64-qr");

        verify(qrCodeService, times(1)).generateQRCode(anyString());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    void shouldThrowExceptionWhenTicketNotFound() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> ticketService.getTicketById(1L));

        verify(ticketRepository, times(1)).findById(1L);
    }

    @Test
    void shouldUpdateTicketStatus() {
        Ticket existing = new Ticket();
        existing.setId(5L);
        existing.setStatus(TicketStatus.CONFIRMED);

        when(ticketRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TicketDTO updated = ticketService.updateTicketStatus(5L, TicketStatus.CANCELLED);

        assertThat(updated).isNotNull();
        assertThat(updated.getStatus()).isEqualTo(TicketStatus.CANCELLED);

        ArgumentCaptor<Ticket> captor = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(TicketStatus.CANCELLED);
    }
}
