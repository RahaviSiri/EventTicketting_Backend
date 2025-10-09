package com.SpringBoot.SeatingService.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.SpringBoot.SeatingService.model.SeatingChart;
import com.SpringBoot.SeatingService.repository.SeatingChartRepository;

@ExtendWith(MockitoExtension.class)
class SeatingChartServiceUnitTest {

    @Mock
    SeatingChartRepository seatingChartRepository;

    @InjectMocks
    SeatingChartService seatingChartService;

    @Test
    void getSeatingChartById_returnsChart() {
        SeatingChart c = new SeatingChart();
        c.setId(1L);
        c.setEventId(5L);
        when(seatingChartRepository.findById(1L)).thenReturn(Optional.of(c));

        var res = seatingChartService.getSeatingChartById(1L);
        assertThat(res).isNotNull();
        assertThat(res.getEventId()).isEqualTo(5L);
    }
}
