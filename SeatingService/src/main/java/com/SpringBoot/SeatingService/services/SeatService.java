package com.SpringBoot.SeatingService.services;

import com.SpringBoot.SeatingService.model.Seat;
import com.SpringBoot.SeatingService.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SeatService {

    @Autowired
    private SeatRepository seatRepository;

    // Get all seats by SeatingChart ID
    public List<Seat> getSeatsByChartId(Long seatingChartId) {
        return seatRepository.findBySeatingChartId(seatingChartId);
    }

    // Book a seat by seat ID (set status to "booked")
    public boolean bookSeat(Long seatId) {
        Optional<Seat> seatOpt = seatRepository.findById(seatId);
        if (seatOpt.isPresent()) {
            Seat seat = seatOpt.get();
            if ("available".equalsIgnoreCase(seat.getStatus())) {
                seat.setStatus("booked");
                seatRepository.save(seat);
                return true; // booking successful
            }
            return false; // seat already booked
        }
        return false; // seat not found
    }

    // Check if seat is available by seat ID
    public boolean isSeatAvailable(Long seatId) {
        Optional<Seat> seatOpt = seatRepository.findById(seatId);
        return seatOpt.map(seat -> "available".equalsIgnoreCase(seat.getStatus())).orElse(false);
    }

}
