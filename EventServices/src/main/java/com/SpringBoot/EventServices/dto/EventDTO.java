package com.SpringBoot.EventServices.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;

@Data
public class EventDTO {

    private Long organizerId;
    private VenueDTO venue;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalTime startTime;
    private LocalDate endDate;
    private LocalTime endTime;
}
