package com.SpringBoot.SeatingService.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "seating_charts")
public class SeatingChart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Store Event ID from Event Service
    private Long eventId;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "TEXT")
    private String layoutJson; 
    // Stores seating layout in JSON format

    private LocalDateTime createdAt;
}
