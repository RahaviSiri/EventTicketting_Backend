package com.event.TicketService.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data               // Generates getters, setters, toString, equals, hashCode
@NoArgsConstructor  // Generates default constructor
@AllArgsConstructor // Generates constructor with all fields
public class CreateTicketRequest {

    private Long eventId;
    private Long userId;
    private List<String> seatNumbers;
    private Double price;
    private LocalDateTime eventDate;
    private String venueName;
    private String eventName;
    
}
