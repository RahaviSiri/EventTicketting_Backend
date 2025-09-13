package com.SpringBoot.SeatingService.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.SpringBoot.SeatingService.model.SeatingChart;
import com.SpringBoot.SeatingService.repository.SeatingChartRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.time.Duration;
import jakarta.transaction.Transactional;

@Service
public class SeatReleaseScheduler {

    @Autowired
    private SeatingChartRepository seatingChartRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // Reservation timeout in minutes
    private final Duration reservationTimeout = Duration.ofMinutes(5);

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void releaseExpiredSeats() {
        List<SeatingChart> charts = seatingChartRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (SeatingChart chart : charts) {
            try {
                JsonNode root = objectMapper.readTree(chart.getLayoutJson());

                ArrayNode seats = root.has("seats") && root.get("seats").isArray()
                        ? (ArrayNode) root.get("seats")
                        : objectMapper.createArrayNode();

                boolean modified = false;

                for (JsonNode seat : seats) {
                    ObjectNode seatNode = (ObjectNode) seat;
                    if ("reserved".equalsIgnoreCase(seatNode.get("status").asText())
                            && seatNode.has("reservedAt")) {

                        LocalDateTime reservedAt = LocalDateTime.parse(seatNode.get("reservedAt").asText());
                        if (Duration.between(reservedAt, now).compareTo(reservationTimeout) > 0) {
                            seatNode.put("status", "available");
                            seatNode.remove("reservedAt");
                            modified = true;
                        }
                    }
                }

                if (modified) {
                    chart.setLayoutJson(objectMapper.writeValueAsString(root));
                    seatingChartRepository.save(chart);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
