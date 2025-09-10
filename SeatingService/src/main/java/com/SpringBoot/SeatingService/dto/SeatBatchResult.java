package com.SpringBoot.SeatingService.dto;

import java.util.List;

public class SeatBatchResult {
    private boolean success;
    private List<String> unavailableSeats;

    public SeatBatchResult() {}

    public SeatBatchResult(boolean success, List<String> unavailableSeats) {
        this.success = success;
        this.unavailableSeats = unavailableSeats;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<String> getUnavailableSeats() {
        return unavailableSeats;
    }

    public void setUnavailableSeats(List<String> unavailableSeats) {
        this.unavailableSeats = unavailableSeats;
    }
}
