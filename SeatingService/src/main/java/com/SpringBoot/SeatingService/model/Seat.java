package com.SpringBoot.SeatingService.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "seats")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many seats belong to one seating chart
    @ManyToOne
    @JoinColumn(name = "seating_chart_id", nullable = false)
    private SeatingChart seatingChart;

    private String seatNumber; // Unique number/label for the seat
    private String row;
    private String section;
    private String seatType; // VIP, Regular, etc.
    private boolean isAccessible;
    private String status; // available/booked
}
