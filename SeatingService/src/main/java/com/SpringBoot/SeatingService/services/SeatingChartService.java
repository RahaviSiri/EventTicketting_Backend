package com.SpringBoot.SeatingService.services;

// import com.SpringBoot.SeatingService.model.Seat;
import com.SpringBoot.SeatingService.model.SeatingChart;
// import com.SpringBoot.SeatingService.repository.SeatRepository;
import com.SpringBoot.SeatingService.repository.SeatingChartRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.time.Duration;
// import java.util.List;
import java.util.Optional;

@Service
public class SeatingChartService {

    @Autowired
    private SeatingChartRepository seatingChartRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // Reservation timeout (default 5 minutes). Replaceable by external config if needed.
    private final Duration reservationTimeout = Duration.ofMinutes(5);

    // @Autowired
    // private SeatRepository seatRepository;

    // Create or update seating chart
    public SeatingChart saveOrUpdateLayout(Long eventId, String layoutJson) {
        Optional<SeatingChart> existingChartOpt = seatingChartRepository.findByEventId(eventId);
        System.out.println("Layout JSON received: " + layoutJson);
        SeatingChart chart;
        if (existingChartOpt.isPresent()) {
            chart = existingChartOpt.get();
            chart.setLayoutJson(layoutJson);
        } else {
            chart = new SeatingChart();
            chart.setEventId(eventId);
            chart.setLayoutJson(layoutJson);
            chart.setCreatedAt(LocalDateTime.now());
        }
        return seatingChartRepository.save(chart);
    }

    // Get by ID
    public SeatingChart getSeatingChartById(Long id) {
        return seatingChartRepository.findById(id).orElse(null);
    }

    // Delete
    @Transactional
    public boolean deleteSeatingChart(Long id) {
        if (seatingChartRepository.existsById(id)) {
            // Delete all seats associated with this seating chart
            // List<Seat> seats = seatRepository.findBySeatingChartId(id);
            // seatRepository.deleteAll(seats);
            
            // Now delete the seating chart
            seatingChartRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Get by Event ID
    public SeatingChart getSeatingChartByEventId(Long id) {
        return seatingChartRepository.findByEventId(id).orElse(null);
    }


     /**
     * Reserve a seat (mark as reserved).
     */
    @Transactional
    public boolean reserveSeat(Long eventId, String seatNumber) {
        SeatingChart chart = seatingChartRepository.findByEventId(eventId)
                .orElseThrow(() -> new RuntimeException("Chart not found"));

        try {
            JsonNode root = objectMapper.readTree(chart.getLayoutJson());
            System.out.println("Root JSON: " + root.toString());
            ArrayNode seats = (ArrayNode) root.get("seats");


            boolean updated = false;
            LocalDateTime now = LocalDateTime.now();
            for (JsonNode seat : seats) {
                ObjectNode seatNode = (ObjectNode) seat;
                if (seatNode.get("seatNumber").asText().equals(seatNumber)) {
                    String status = seatNode.get("status").asText();

                    // If available, reserve immediately
                    if ("available".equalsIgnoreCase(status)) {
                        seatNode.put("status", "reserved");
                        // seatNode.put("reservedAt", now.toString());
                        updated = true;
                        break;
                    }

                    // If already reserved, check expiry and allow new reservation if expired
                    if ("reserved".equalsIgnoreCase(status) && seatNode.has("reservedAt")) {
                        LocalDateTime reservedAt = LocalDateTime.parse(seatNode.get("reservedAt").asText());
                        if (isExpired(reservedAt, now)) {
                            seatNode.put("status", "reserved");
                            // seatNode.put("reservedAt", now.toString());
                            updated = true;
                            break;
                        }
                    }

                    // Otherwise (booked or actively reserved) cannot reserve
                    return false;
                }
            }

            if (updated) {
                chart.setLayoutJson(objectMapper.writeValueAsString(root));
                seatingChartRepository.save(chart);
                System.out.println("Seat " + seatNumber + " reserved for event " + eventId);
                return true;
            }

            return false;

        } catch (Exception e) {
            throw new RuntimeException("Failed to reserve seat", e);
        }
    }

    /**
     * Check if a reservedAt timestamp is expired compared to now and configured timeout.
     */
    private boolean isExpired(LocalDateTime reservedAt, LocalDateTime now) {
        return Duration.between(reservedAt, now).compareTo(reservationTimeout) > 0;
    }

    /**
     * Confirm payment → reserved → booked
     */
    @Transactional
    public boolean confirmSeat(Long eventId, String seatNumber) {
        return updateSeatStatus(eventId, seatNumber, "reserved", "booked");
    }

    /**
     * Payment failed/timeout → reserved → available
     */
    @Transactional
    public boolean releaseSeat(Long eventId, String seatNumber) {
        return updateSeatStatus(eventId, seatNumber, "reserved", "available");
    }

    /**
     * Generic seat status update with check.
     */
    private boolean updateSeatStatus(Long eventId, String seatNumber, String fromStatus, String toStatus) {
        SeatingChart chart = seatingChartRepository.findByEventId(eventId)
                .orElseThrow(() -> new RuntimeException("Chart not found"));

        try {
            JsonNode root = objectMapper.readTree(chart.getLayoutJson());
            ArrayNode seats = (ArrayNode) root.get("seats");

            boolean updated = false;
            LocalDateTime now = LocalDateTime.now();
            for (JsonNode seat : seats) {
                ObjectNode seatNode = (ObjectNode) seat;
                if (seatNode.get("seatNumber").asText().equals(seatNumber)) {
                    String status = seatNode.get("status").asText();

                    // If expected 'fromStatus' is 'reserved', also allow if it was reserved but expired
                    if (!fromStatus.equalsIgnoreCase(status)) {
                        // Allow transition if fromStatus was 'reserved' and reservation expired
                        if ("reserved".equalsIgnoreCase(fromStatus) && "reserved".equalsIgnoreCase(status) && seatNode.has("reservedAt")) {
                            LocalDateTime reservedAt = LocalDateTime.parse(seatNode.get("reservedAt").asText());
                            if (isExpired(reservedAt, now)) {
                                // treat as available for transition purposes
                            } else {
                                return false; // still reserved and not expired
                            }
                        } else {
                            return false; // invalid transition
                        }
                    }

                    seatNode.put("status", toStatus);
                    // remove reservedAt when moving to available or booked
                    if (seatNode.has("reservedAt") && ("available".equalsIgnoreCase(toStatus) || "booked".equalsIgnoreCase(toStatus))) {
                        seatNode.remove("reservedAt");
                    }

                    updated = true;
                    break;
                }
            }

            if (updated) {
                chart.setLayoutJson(objectMapper.writeValueAsString(root));
                seatingChartRepository.save(chart);
                return true;
            }

            return false;

        } catch (Exception e) {
            throw new RuntimeException("Failed to update seat status", e);
        }
    }
}
