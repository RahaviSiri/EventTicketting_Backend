package com.SpringBoot.AdminService.dto;
import java.time.LocalDate;
import java.time.LocalTime;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventAdminDto {
    private Long id;
    private Long organizerId;
    private String venueName;
    private String eventName;
    private String description;
    private LocalDate startDate;
    private LocalTime startTime;
    private LocalDate endDate;
    private LocalTime endTime;
    private String status;
    private String category;
    private LocalDateTime createdAt;

}