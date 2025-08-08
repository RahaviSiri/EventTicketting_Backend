package com.SpringBoot.EventServices.dto;

import lombok.Data;

@Data
public class SeatingChartRequest {
    private Long eventId;
    private String layoutJson;
}
