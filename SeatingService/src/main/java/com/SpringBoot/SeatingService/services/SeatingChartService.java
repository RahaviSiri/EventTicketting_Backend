package com.SpringBoot.SeatingService.services;

import com.SpringBoot.SeatingService.model.Seat;
import com.SpringBoot.SeatingService.model.SeatingChart;
import com.SpringBoot.SeatingService.repository.SeatRepository;
import com.SpringBoot.SeatingService.repository.SeatingChartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SeatingChartService {

    @Autowired
    private SeatingChartRepository seatingChartRepository;

    @Autowired
    private SeatRepository seatRepository;

    // Create new seating chart
    public SeatingChart createSeatingChart(SeatingChart seatingChart) {
        seatingChart.setCreatedAt(LocalDateTime.now());
        // Save chart first to get ID
        SeatingChart savedChart = seatingChartRepository.save(seatingChart);
        // Parse layoutJson to create Seat entities
        List<Seat> seats = new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(savedChart.getLayoutJson());

            JsonNode seatsNode = root.get("seats");
            if (seatsNode != null && seatsNode.isArray()) {
                for (JsonNode seatNode : seatsNode) {
                    Seat seat = new Seat();
                    seat.setSeatingChart(savedChart);
                    seat.setSeatNumber(seatNode.get("seatNumber").asText());
                    seat.setRow(seatNode.get("row").asText());
                    seat.setSection(seatNode.get("section").asText());
                    seat.setSeatType(seatNode.get("seatType").asText());
                    seat.setAccessible(seatNode.get("isAccessible").asBoolean());
                    seat.setStatus(seatNode.get("status").asText());
                    seats.add(seat);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error parsing seating chart layout JSON", e);
        }
        // Save all seats
        seatRepository.saveAll(seats);
        return savedChart;
    }

    // Get by ID
    public SeatingChart getSeatingChartById(Long id) {
        return seatingChartRepository.findById(id).orElse(null);
    }

    // Update
    public SeatingChart updateSeatingChart(Long id, SeatingChart updatedChart) {
        Optional<SeatingChart> optionalChart = seatingChartRepository.findById(id);
        if (optionalChart.isPresent()) {
            SeatingChart existingChart = optionalChart.get();

            // Update layout JSON
            existingChart.setLayoutJson(updatedChart.getLayoutJson());

            // Save updated chart first
            SeatingChart savedChart = seatingChartRepository.save(existingChart);

            // Delete old seats linked to this chart
            List<Seat> oldSeats = seatRepository.findBySeatingChartId(savedChart.getId());
            seatRepository.deleteAll(oldSeats);

            // Parse new layoutJson and recreate seats
            List<Seat> newSeats = new ArrayList<>();
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(savedChart.getLayoutJson());
                JsonNode seatsNode = root.get("seats");
                if (seatsNode != null && seatsNode.isArray()) {
                    for (JsonNode seatNode : seatsNode) {
                        Seat seat = new Seat();
                        seat.setSeatingChart(savedChart);
                        seat.setSeatNumber(seatNode.get("seatNumber").asText());
                        seat.setRow(seatNode.get("row").asText());
                        seat.setSection(seatNode.get("section").asText());
                        seat.setSeatType(seatNode.get("seatType").asText());
                        seat.setAccessible(seatNode.get("isAccessible").asBoolean());
                        seat.setStatus(seatNode.get("status").asText());
                        newSeats.add(seat);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Error parsing seating chart layout JSON", e);
            }
            // Save new seats
            seatRepository.saveAll(newSeats);
            return savedChart;
        }
        return null;
    }

    // Delete
    @Transactional
    public boolean deleteSeatingChart(Long id) {
        if (seatingChartRepository.existsById(id)) {
            // Delete all seats associated with this seating chart
            List<Seat> seats = seatRepository.findBySeatingChartId(id);
            seatRepository.deleteAll(seats);
            
            // Now delete the seating chart
            seatingChartRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
