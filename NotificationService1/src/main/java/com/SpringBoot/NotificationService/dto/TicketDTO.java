package com.SpringBoot.NotificationService.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TicketDTO {
    private Long id;
    private String eventName;
    private LocalDateTime eventDate;
    private String seatNumbers;  // store as "A1,A2,A3"
    private Double price;
    private String venueName;
    private String qrCode; 
    private Long userId;
}
