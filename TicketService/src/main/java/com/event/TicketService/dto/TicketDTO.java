package com.event.TicketService.dto;

import com.event.TicketService.model.TicketStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketDTO {
    private Long id;
    private String ticketNumber;
    private Long eventId;
    private Long userId;
    private String seatNumbers;
    private Double price;
    private TicketStatus status;
    private LocalDateTime purchaseDate;
    private String qrCode;
    private String venue_name;
    private String event_name;
}
