package com.SpringBoot.EventServices.repository;

import com.SpringBoot.EventServices.model.Event;
import com.SpringBoot.EventServices.model.Venue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Test
    void shouldSaveAndFindByOrganizer() {
        Event e = new Event();
        e.setName("N");
        e.setOrganizerId(7L);
        Venue v = new Venue();
        v.setName("V");
        e.setVenue(v);
        e.setStartDate(LocalDate.now());
        e.setStartTime(LocalTime.now());
        e.setEndDate(LocalDate.now());
        e.setEndTime(LocalTime.now());

        eventRepository.save(e);

        assertThat(eventRepository.findByOrganizerId(7L)).isNotEmpty();
    }
}
