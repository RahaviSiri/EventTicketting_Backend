package com.SpringBoot.EventServices.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long organizerId;      // comes from User Service
    // Venue entered for this event
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "venue_id", referencedColumnName = "id")
    private Venue venue;  
           
    private String name;
    private String description;

    private LocalDate startDate;
    private LocalTime startTime;
    private LocalDate endDate;
    private LocalTime endTime;

    private Long seatingChartId;   // comes from Seating Service
    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
