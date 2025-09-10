package com.SpringBoot.OrderService.models;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment in DB
    private Long id;

    private Long eventId;
    private Long userId;
    private Long ticketId;
    private String attendeeName;
    private String attendeeEmail;
    private String ticketType;
    private Double price;
    private String status;   // e.g., "Paid", "Pending"
    private boolean checkIn;
    private LocalDateTime createdAt;
}
