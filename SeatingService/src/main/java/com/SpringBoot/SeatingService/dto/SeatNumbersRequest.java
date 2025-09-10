package com.SpringBoot.SeatingService.dto;

import java.util.List;

public class SeatNumbersRequest {
    private List<String> seatNumbers;

    public SeatNumbersRequest() {
    }

    public List<String> getSeatNumbers() {
        return seatNumbers;
    }

    public void setSeatNumbers(List<String> seatNumbers) {
        this.seatNumbers = seatNumbers;
    }
}
