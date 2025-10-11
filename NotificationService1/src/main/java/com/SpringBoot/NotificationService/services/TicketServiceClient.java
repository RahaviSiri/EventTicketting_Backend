package com.SpringBoot.NotificationService.services;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.SpringBoot.NotificationService.dto.TicketDTO;

@FeignClient(name = "TicketService")
public interface TicketServiceClient {

    @GetMapping("/api/tickets/{id}")
    TicketDTO getTicketById(@PathVariable Long id);

    @GetMapping("/api/tickets/upcoming")
    List<TicketDTO> getTicketsForUpcomingEvents();

    @GetMapping("/api/tickets/event/{eventId}")
    List<TicketDTO> getTicketsByEventId(@PathVariable Long eventId);
}
    

