package com.SpringBoot.AdminService.service;

import com.SpringBoot.AdminService.client.*;
import com.SpringBoot.AdminService.dto.AdminDashboardDto;
import com.SpringBoot.AdminService.dto.EventAdminDto;
import com.SpringBoot.AdminService.dto.OrganizerManagementDto;
import com.SpringBoot.AdminService.dto.UserDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private static final Logger log = LoggerFactory.getLogger(AdminService.class);

    private final UserClient userClient;
    private final EventClient eventClient;
    private final PaymentClient paymentClient;
    private final TicketClient ticketClient;

    public AdminService(UserClient userClient, EventClient eventClient, PaymentClient paymentClient,
            TicketClient ticketClient) {
        this.userClient = userClient;
        this.eventClient = eventClient;
        this.paymentClient = paymentClient;
        this.ticketClient = ticketClient;
    }

    public List<Map<String, Object>> getSignupsVsEventsLast6Months() {
        List<Map<String, Object>> signups = userClient.getSignupsLast6Months();
        List<Map<String, Object>> events = eventClient.getEventsLast6Months();

        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < signups.size(); i++) {
            Map<String, Object> s = signups.get(i);
            Map<String, Object> e = events.get(i);

            result.add(Map.of(
                    "month", s.get("month"),
                    "signups", s.get("signups"),
                    "events", e.get("events")));
        }
        return result;
    }

    public AdminDashboardDto getDashboardStats(String range) {
        try {

            AdminDashboardDto dto = new AdminDashboardDto();

            // KPIs
            Map<String, Object> revenue = paymentClient.getRevenueSummary(range);
            log.info("Revenue summary ({}): {}", range, revenue);
            dto.setTotalRevenue(((Number) revenue.get("total")).longValue());
            dto.setRevenueTrend(((Number) revenue.get("trend")).doubleValue());

            Map<String, Object> events = eventClient.getEventsSummary(range);
            log.info("Events summary ({}): {}", range, events);
            dto.setActiveEvents(((Number) events.get("total")).intValue());
            dto.setEventsTrend(((Number) events.get("trend")).doubleValue());

            Map<String, Object> organizers = userClient.getOrganizersSummary(range);
            log.info("Organizers summary ({}): {}", range, organizers);
            dto.setTotalOrganizers(((Number) organizers.get("total")).intValue());
            dto.setOrganizersTrend(((Number) organizers.get("trend")).doubleValue());

            Map<String, Object> tickets = ticketClient.getTicketsSummary(range);
            log.info("Tickets summary ({}): {}", range, tickets);
            dto.setTotalTicketsSold(((Number) tickets.get("total")).intValue());
            dto.setTicketsTrend(((Number) tickets.get("trend")).doubleValue());

            // Charts
            List<Map<String, Object>> revenueOverTime = paymentClient.getRevenueLast6Months();
            log.info("Revenue over time (6 months): {}", revenueOverTime);
            dto.setRevenueOverTime(revenueOverTime);

            List<Map<String, Object>> userEventStats = getSignupsVsEventsLast6Months();
            log.info("Signups vs Events (6 months): {}", userEventStats);
            dto.setUserEventStats(userEventStats);

            return dto;
        } catch (Exception e) {
            log.error(" Error while fetching dashboard stats for range {}", range, e);
            throw new RuntimeException("Failed to fetch dashboard stats", e);
        }
    }

    public Page<OrganizerManagementDto> getOrganizersWithEventCounts(Pageable pageable) {
        // Get paginated organizers from User Service
        Page<UserDTO> organizersPage = userClient.getAllOrganizers(
                pageable.getPageNumber(),
                pageable.getPageSize());

        // Convert to OrganizerManagementDto with event counts
        List<OrganizerManagementDto> organizersWithCounts = organizersPage.getContent()
                .stream()
                .map(organizer -> {
                    Long activeCount = eventClient.getActiveEventsCount(organizer.getId());
                    Long pendingCount = eventClient.getPendingEventsCount(organizer.getId());

                    return new OrganizerManagementDto(
                            organizer.getId(),
                            organizer.getName(),
                            organizer.getEmail(),
                            activeCount,
                            pendingCount);
                })
                .collect(Collectors.toList());

        return new PageImpl<>(
                organizersWithCounts,
                pageable,
                organizersPage.getTotalElements());
    }

    // public Page<EventAdminDto> getAllEvents(int page, int size) {
    //     return eventClient.getAllEventsForAdmin(page, size);
    // }

    public Page<Map<String, Object>> getAllEventsWithUserDetails(int page, int size) {
        Map<String, Object> response = eventClient.getAllEventsForAdminRaw(page, size);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> content = (List<Map<String, Object>>) response.get("content");

        @SuppressWarnings("unchecked")
        Map<String, Object> pageableMap = (Map<String, Object>) response.get("pageable");

        int pageNumber = pageableMap != null ? (int) pageableMap.getOrDefault("pageNumber", page) : page;
        int pageSize = pageableMap != null ? (int) pageableMap.getOrDefault("pageSize", size) : size;
        long totalElements = ((Number) response.getOrDefault("totalElements", content.size())).longValue();

        List<Map<String, Object>> eventMaps = content.stream().map(e -> {
            Long organizerId = ((Number) e.get("organizerId")).longValue();
            UserDTO user = userClient.getUsernameAndEmailById(organizerId);

            Map<String, Object> map = new HashMap<>();
            map.put("id", e.get("id"));
            map.put("eventName", e.get("eventName"));
            map.put("venueName", e.get("venueName"));
            map.put("description", e.get("description"));
            map.put("startDate", e.get("startDate"));
            map.put("startTime", e.get("startTime"));
            map.put("endDate", e.get("endDate"));
            map.put("endTime", e.get("endTime"));
            map.put("status", e.get("status"));
            map.put("category", e.get("category"));
            map.put("createdAt", e.get("createdAt"));
            map.put("organizerId", e.get("organizerId"));
            map.put("organizerName", user != null ? user.getName() : null);
            map.put("organizerEmail", user != null ? user.getEmail() : null);
            return map;
        }).toList();

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return new PageImpl<>(eventMaps, pageable, totalElements);
    }

    public Map<String, Object> getEventById(Long id) {
        Map<String, Object> event = eventClient.getEventById(id);

        if (event == null) {
            return null;
        }

        // Safely extract organizer ID
        Object organizerIdObj = event.get("organizerId");
        if (organizerIdObj instanceof Number) {
            Long organizerId = ((Number) organizerIdObj).longValue();

            // Fetch organizer details
            UserDTO organizer = userClient.getUsernameAndEmailById(organizerId);
            if (organizer != null) {
                event.put("organizerName", organizer.getName());
                event.put("organizerEmail", organizer.getEmail());
            }
        }

        return event;
    }

    public void updateEventStatus(Long eventId, String status) {
        eventClient.updateEventStatus(eventId, status);
    }

}
