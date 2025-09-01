package com.event.TicketService.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ticketNumber;

    @Column(nullable = false)
    private Long eventId;

    @Column(nullable = false)
    private Long userId;

    @ElementCollection
    @CollectionTable(name = "ticket_seats", joinColumns = @JoinColumn(name = "ticket_id"))
    @Column(name = "seat_number")
    private List<String> seatNumbers;

    @Column(nullable = false)
    private String ticketType; // VIP, Regular, Premium

    @Column(nullable = false)
    private Double price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status;

    @Column(nullable = false)
    private LocalDateTime purchaseDate;

    @Column(nullable = false)
    private LocalDateTime eventDate;

    @Column
    private String qrCode;

    @Column
    private String venueName;

    @Column
    private String eventName;
}
