package com.SpringBoot.EventServices.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.SpringBoot.EventServices.model.Event;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class EventAdminDto {
    private Long id;
    private Long organizerId;
    private String venueName;
    private String eventName;
    private String description;
    private LocalDate startDate;
    private LocalTime startTime;
    private LocalDate endDate;
    private LocalTime endTime;
    private String status;
    private String category;
    private LocalDateTime createdAt;

    // Constructor to convert Event entity to EventAdminDto
    public EventAdminDto(Event event) {
        this.id = event.getId();
        this.organizerId = event.getOrganizerId();
        this.venueName = event.getVenue() != null ? event.getVenue().getName() : null;
        this.eventName = event.getName();
        this.description = event.getDescription();
        this.startDate = event.getStartDate();
        this.startTime = event.getStartTime();
        this.endDate = event.getEndDate();
        this.endTime = event.getEndTime();
        this.status = event.getStatus();
        this.category = event.getCategory();
        this.createdAt = event.getCreatedAt();
    }
}