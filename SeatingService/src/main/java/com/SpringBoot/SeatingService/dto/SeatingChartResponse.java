// src/main/java/com/SpringBoot/SeatingService/dto/SeatingChartResponse.java
package com.SpringBoot.SeatingService.dto;

import java.time.LocalDateTime;

public class SeatingChartResponse {
    private Long id;
    private Long eventId;
    private String layoutJson;
    private LocalDateTime createdAt;

    public SeatingChartResponse() {}

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
    public String getLayoutJson() { return layoutJson; }
    public void setLayoutJson(String layoutJson) { this.layoutJson = layoutJson; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
