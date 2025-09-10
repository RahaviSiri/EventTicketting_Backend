package com.SpringBoot.OrderService.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderCreateDTO {
    private Long eventId;
    private Long userId;
    private Long ticketId;
    private String ticketType;
    private Double price;
}
