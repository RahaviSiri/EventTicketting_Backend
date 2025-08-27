package com.event.TicketService.dto;

import java.time.LocalDateTime;

public class CreateTicketRequest {
    
    private Long eventId;
    private Long userId;
    private String seatNumber;
    private String ticketType;
    private Double price;
    private LocalDateTime eventDate;
    private String venueName;
    private String eventName;
    
    // Constructors
    public CreateTicketRequest() {}
    
    public CreateTicketRequest(Long eventId, Long userId, String seatNumber, 
                              String ticketType, Double price, LocalDateTime eventDate,
                              String venueName, String eventName) {
        this.eventId = eventId;
        this.userId = userId;
        this.seatNumber = seatNumber;
        this.ticketType = ticketType;
        this.price = price;
        this.eventDate = eventDate;
        this.venueName = venueName;
        this.eventName = eventName;
    }
    
    // Getters and Setters
    public Long getEventId() {
        return eventId;
    }
    
    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getSeatNumber() {
        return seatNumber;
    }
    
    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }
    
    public String getTicketType() {
        return ticketType;
    }
    
    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }
    
    public Double getPrice() {
        return price;
    }
    
    public void setPrice(Double price) {
        this.price = price;
    }
    
    public LocalDateTime getEventDate() {
        return eventDate;
    }
    
    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }
    
    public String getVenueName() {
        return venueName;
    }
    
    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }
    
    public String getEventName() {
        return eventName;
    }
    
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
}
