package com.event.TicketService.repository;

import com.event.TicketService.model.Ticket;
import com.event.TicketService.model.TicketStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TicketRepositoryTest {

    @Autowired
    private TicketRepository ticketRepository;

    @Test
    void shouldSaveAndFindByUserId() {
        Ticket t = new Ticket();
        t.setTicketNumber("T1");
        t.setEventId(2L);
        t.setUserId(3L);
        t.setSeatNumbers("A1");
        t.setPrice(20.0);
        t.setStatus(TicketStatus.CONFIRMED);
        t.setPurchaseDate(LocalDateTime.now());
        t.setQrCode("qr");
        t.setEventDate(LocalDateTime.now().plusDays(1));
        t.setVenue_name("V");
        t.setEvent_name("E");

        ticketRepository.save(t);

        List<Ticket> found = ticketRepository.findByUserId(3L);
        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getUserId()).isEqualTo(3L);
    }

    @Test
    void shouldFindByEventDateBetween() {
        LocalDateTime now = LocalDateTime.now();
        Ticket t = new Ticket();
        t.setTicketNumber("T2");
        t.setEventId(5L);
        t.setUserId(6L);
        t.setSeatNumbers("B1");
        t.setPrice(30.0);
        t.setStatus(TicketStatus.CONFIRMED);
        t.setPurchaseDate(now.minusDays(1));
        t.setQrCode("qr2");
        t.setEventDate(now.plusHours(12));
        t.setVenue_name("V2");
        t.setEvent_name("E2");

        ticketRepository.save(t);

        List<Ticket> found = ticketRepository.findByEventDateBetween(now, now.plusDays(1));
        assertThat(found).isNotEmpty();
    }
}
