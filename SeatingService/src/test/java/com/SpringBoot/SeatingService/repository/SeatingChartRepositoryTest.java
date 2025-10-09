package com.SpringBoot.SeatingService.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.SpringBoot.SeatingService.model.SeatingChart;

@DataJpaTest
class SeatingChartRepositoryTest {

    @Autowired
    SeatingChartRepository seatingChartRepository;

    @Test
    void saveAndFindByEventId() {
        SeatingChart c = new SeatingChart();
        c.setEventId(10L);
        c.setLayoutJson("{\"seats\": []}");

        var saved = seatingChartRepository.save(c);
        assertThat(saved.getId()).isNotNull();

        var found = seatingChartRepository.findByEventId(10L);
        assertThat(found).isPresent();
    }
}
