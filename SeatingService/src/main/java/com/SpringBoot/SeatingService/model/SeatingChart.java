package com.SpringBoot.SeatingService.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "seating_charts")
public class SeatingChart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Store Event ID from Event Service
    private Long eventId;

    // @Lob
    // @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "TEXT")
    private String layoutJson; 
    // Stores seating layout in JSON format

    private LocalDateTime createdAt;

    // // Optimistic locking to prevent concurrent update conflicts
    // @Version
    // private Long version;
}
