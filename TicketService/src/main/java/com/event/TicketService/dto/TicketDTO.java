package com.event.TicketService.dto;

import com.event.TicketService.model.TicketStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data               // Generates getters, setters, toString, equals, hashCode
@NoArgsConstructor  // Default constructor
@AllArgsConstructor // Constructor with all fields
public class TicketDTO {

    private Long id;
    private String ticketNumber;
    private Long eventId;
    private Long userId;
    private List<String> seatNumbers;
    private String ticketType;
    private Double price;
    private TicketStatus status;
    private LocalDateTime purchaseDate;
    private LocalDateTime eventDate;
    private String qrCode;
    private String venueName;
    private String eventName;
}
