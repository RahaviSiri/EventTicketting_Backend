package com.SpringBoot.AdminService.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardDto {
    private long totalRevenue;
    private double revenueTrend; // % change
    private int activeEvents;
    private double eventsTrend;
    private int totalOrganizers;
    private double organizersTrend;
    private int totalTicketsSold;
    private double ticketsTrend;

    private List<Map<String, Object>> revenueOverTime;  // e.g., [{month: "Sep", revenue: 50000}, ...]
    private List<Map<String, Object>> userEventStats;   // e.g., [{organizer: "Alice", events: 5}, ...]
}
