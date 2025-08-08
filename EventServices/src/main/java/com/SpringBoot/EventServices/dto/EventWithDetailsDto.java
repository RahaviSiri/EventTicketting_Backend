package com.SpringBoot.EventServices.dto;

import com.SpringBoot.EventServices.model.Event;
import com.SpringBoot.EventServices.model.Venue;

import lombok.Data;

@Data
public class EventWithDetailsDto {
    private Event event;
    private Venue venue;
    private Long seatingChartId;
}
