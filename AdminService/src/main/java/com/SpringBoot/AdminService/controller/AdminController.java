package com.SpringBoot.AdminService.controller;

import com.SpringBoot.AdminService.dto.AdminDashboardDto;
import com.SpringBoot.AdminService.dto.EventAdminDto;
import com.SpringBoot.AdminService.dto.OrganizerManagementDto;
import com.SpringBoot.AdminService.service.AdminService;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardDto> getDashboard(
            @RequestParam(defaultValue = "last7days") String range) {
        return ResponseEntity.ok(adminService.getDashboardStats(range));
    }

    @GetMapping("/userManagement/organizers")
    public ResponseEntity<Page<OrganizerManagementDto>> getOrganizersWithEventCounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageRequest pageable = PageRequest.of(page, size);
        Page<OrganizerManagementDto> organizers = adminService.getOrganizersWithEventCounts(pageable);

        return ResponseEntity.ok(organizers);
    }

    // Get all events for admin
    @GetMapping("/userManagement/events")
    public ResponseEntity<Page<Map<String, Object>>> getAllEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Map<String, Object>> events = adminService.getAllEventsWithUserDetails(page, size);
        return ResponseEntity.ok(events);
    }

    // @GetMapping("/userManagement/events/{eventId}")
    // public ResponseEntity<Map<String, Object>> getEventById(@PathVariable Long
    // eventId) {
    // Map<String, Object> eventDetails =
    // adminService.getEventWithUserDetails(eventId);
    // return ResponseEntity.ok(eventDetails);
    // }

    @GetMapping("/userManagement/pendingEvents")
    public ResponseEntity<Page<Map<String, Object>>> getPendingEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Get all events with user details as a Page
        Page<Map<String, Object>> allEvents = adminService.getAllEventsWithUserDetails(page, size);

        // Filter only PENDING events
        List<Map<String, Object>> pendingList = allEvents.getContent().stream()
                .filter(e -> "PENDING".equalsIgnoreCase((String) e.get("status")))
                .toList();

        // Reconstruct a Page object for pagination metadata
        PageRequest pageable = PageRequest.of(page, size);
        Page<Map<String, Object>> pendingPage = new PageImpl<>(pendingList, pageable, pendingList.size());

        return ResponseEntity.ok(pendingPage);
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<Map<String, Object>> getEventById(@PathVariable Long id) {
        Map<String, Object> event = adminService.getEventById(id);

        if (event == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(event);
    }

    @PutMapping("/events/{eventId}/approve")
    public ResponseEntity<String> approveEvent(@PathVariable Long eventId) {
        adminService.updateEventStatus(eventId, "APPROVED");
        return ResponseEntity.ok("Event approved successfully");
    }
    @PutMapping("/events/{eventId}/reject")
    public ResponseEntity<String> rejectEvent(@PathVariable Long eventId) {
        adminService.updateEventStatus(eventId, "REJECTED");
        return ResponseEntity.ok("Event rejected successfully");
    }

}
