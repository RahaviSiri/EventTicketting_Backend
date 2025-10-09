package com.SpringBoot.SeatingService.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.SpringBoot.SeatingService.services.SeatingChartService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = SeatingChartController.class)
class SeatingChartControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    SeatingChartService seatingChartService;

    @Test
    void getById_returnsNotFound() throws Exception {
        when(seatingChartService.getSeatingChartById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/seating-charts/999")).andExpect(status().isNotFound());
    }
}
