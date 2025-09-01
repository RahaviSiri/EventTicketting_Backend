package com.event.TicketService.dto;

import com.event.TicketService.model.TicketStatus;
import java.time.LocalDateTime;
import java.util.List;

public class TicketDTO {
    
    private Long id;
    private String ticketNumber;
    private Long eventId;
    private Long userId;
    private List<String> seatNumbers;
    private String ticketType;
    private Double price;
    private TicketStatus status;
    private LocalDateTime purchaseDate;
    private LocalDateTime eventDate;
    private String qrCode;
    private String venueName;
    private String eventName;
    
    // Constructors
    public TicketDTO() {}
    
    public TicketDTO(Long id, String ticketNumber, Long eventId, Long userId, 
                 List<String> seatNumbers, String ticketType, Double price, 
                     TicketStatus status, LocalDateTime purchaseDate, 
                     LocalDateTime eventDate, String qrCode, String venueName, String eventName) {
        this.id = id;
        this.ticketNumber = ticketNumber;
        this.eventId = eventId;
        this.userId = userId;
        this.seatNumbers = seatNumbers;
        this.ticketType = ticketType;
        this.price = price;
        this.status = status;
        this.purchaseDate = purchaseDate;
        this.eventDate = eventDate;
        this.qrCode = qrCode;
        this.venueName = venueName;
        this.eventName = eventName;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTicketNumber() {
        return ticketNumber;
    }
    
    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }
    
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
    
public List<String> getSeatNumbers() {
    return seatNumbers;
}
    

public void setSeatNumbers(List<String> seatNumbers) {
    this.seatNumbers = seatNumbers;
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
    
    public TicketStatus getStatus() {
        return status;
    }
    
    public void setStatus(TicketStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }
    
    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
    
    public LocalDateTime getEventDate() {
        return eventDate;
    }
    
    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }
    
    public String getQrCode() {
        return qrCode;
    }
    
    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
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
