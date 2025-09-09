package com.SpringBoot.SeatingService.services;

import com.SpringBoot.SeatingService.model.SeatingChart;
import com.SpringBoot.SeatingService.repository.SeatingChartRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class SeatingReservationCleaner {

    @Autowired
    private SeatingChartRepository seatingChartRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // Keep in sync with SeatingChartService default (5 minutes)
    private final Duration reservationTimeout = Duration.ofMinutes(5);

    // Run every minute to release expired reservations
    @Scheduled(fixedDelayString = "PT1M")
    public void cleanExpiredReservations() {
        List<SeatingChart> charts = seatingChartRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (SeatingChart chart : charts) {
            try {
                JsonNode root = objectMapper.readTree(chart.getLayoutJson());
                ArrayNode seats = (ArrayNode) root.get("seats");
                boolean changed = false;

                for (JsonNode seat : seats) {
                    ObjectNode seatNode = (ObjectNode) seat;
                    if (seatNode.has("status") && "reserved".equalsIgnoreCase(seatNode.get("status").asText()) && seatNode.has("reservedAt")) {
                        LocalDateTime reservedAt = LocalDateTime.parse(seatNode.get("reservedAt").asText());
                        if (Duration.between(reservedAt, now).compareTo(reservationTimeout) > 0) {
                            seatNode.put("status", "available");
                            seatNode.remove("reservedAt");
                            changed = true;
                        }
                    }
                }

                if (changed) {
                    chart.setLayoutJson(objectMapper.writeValueAsString(root));
                    seatingChartRepository.save(chart);
                }
            } catch (Exception e) {
                // Log and continue
                System.err.println("Failed to clean chart " + chart.getId() + ": " + e.getMessage());
            }
        }
    }
}
