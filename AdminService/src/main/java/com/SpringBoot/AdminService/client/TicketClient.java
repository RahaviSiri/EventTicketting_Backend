package com.SpringBoot.AdminService.client;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "TicketService", path = "/api/tickets")
public interface TicketClient {
    @GetMapping("/summary")
    Map<String, Object> getTicketsSummary(@RequestParam("range") String range);
}
