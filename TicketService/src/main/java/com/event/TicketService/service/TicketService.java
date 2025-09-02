package com.event.TicketService.service;

import com.event.TicketService.dto.CreateTicketRequest;
import com.event.TicketService.dto.TicketDTO;
import com.event.TicketService.model.Ticket;
import com.event.TicketService.model.TicketStatus;
import com.event.TicketService.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TicketService {
    
    @Autowired
    private TicketRepository ticketRepository;
    
    @Autowired
    private QRCodeService qrCodeService;
    
    // Create a new ticket
    public TicketDTO createTicket(CreateTicketRequest request) {
        
    
        
        // Generate unique ticket number
        String ticketNumber = generateTicketNumber();
        
        // Create ticket
        Ticket ticket = new Ticket();
        ticket.setTicketNumber(ticketNumber);
        ticket.setEventId(request.getEventId());
        ticket.setUserId(request.getUserId());
        ticket.setSeatNumbers(request.getSeatNumbers());
        ticket.setTicketType(request.getTicketType());
        ticket.setPrice(request.getPrice());
        ticket.setStatus(TicketStatus.RESERVED);
        ticket.setPurchaseDate(LocalDateTime.now());
        ticket.setEventDate(request.getEventDate());
        ticket.setVenueName(request.getVenueName());
        ticket.setEventName(request.getEventName());
        
        // Generate QR code
        String qrCode = qrCodeService.generateQRCode(ticketNumber);
        ticket.setQrCode(qrCode);
        
        // Save ticket
        Ticket savedTicket = ticketRepository.save(ticket);
        
        return convertToDTO(savedTicket);
    }
    
    // Get ticket by ID
    public TicketDTO getTicketById(Long id) {
        Optional<Ticket> ticket = ticketRepository.findById(id);
        if (ticket.isPresent()) {
            return convertToDTO(ticket.get());
        }
        throw new RuntimeException("Ticket not found with id: " + id);
    }
    
  
    
    // Get all tickets by user ID
    public List<TicketDTO> getTicketsByUserId(Long userId) {
        List<Ticket> tickets = ticketRepository.findByUserId(userId);
        return tickets.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // Get all tickets by event ID
    public List<TicketDTO> getTicketsByEventId(Long eventId) {
        List<Ticket> tickets = ticketRepository.findByEventId(eventId);
        return tickets.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // Update ticket status
    public TicketDTO updateTicketStatus(Long ticketId, TicketStatus newStatus) {
        Optional<Ticket> ticketOpt = ticketRepository.findById(ticketId);
        if (ticketOpt.isPresent()) {
            Ticket ticket = ticketOpt.get();
            ticket.setStatus(newStatus);
            Ticket updatedTicket = ticketRepository.save(ticket);
            return convertToDTO(updatedTicket);
        }
        throw new RuntimeException("Ticket not found with id: " + ticketId);
    }
    
    // Confirm ticket (change status from RESERVED to CONFIRMED)
    public TicketDTO confirmTicket(Long ticketId) {
        return updateTicketStatus(ticketId, TicketStatus.CONFIRMED);
    }
    
    // Cancel ticket
    public TicketDTO cancelTicket(Long ticketId) {
        return updateTicketStatus(ticketId, TicketStatus.CANCELLED);
    }
    
    // Mark ticket as used
    public TicketDTO markTicketAsUsed(Long ticketId) {
        return updateTicketStatus(ticketId, TicketStatus.USED);
    }
    
    // Refund ticket
    public TicketDTO refundTicket(Long ticketId) {
        return updateTicketStatus(ticketId, TicketStatus.REFUNDED);
    }
    
    // Delete ticket
    public void deleteTicket(Long ticketId) {
        if (ticketRepository.existsById(ticketId)) {
            ticketRepository.deleteById(ticketId);
        } else {
            throw new RuntimeException("Ticket not found with id: " + ticketId);
        }
    }
    
    // Get tickets by status
    public List<TicketDTO> getTicketsByStatus(TicketStatus status) {
        List<Ticket> tickets = ticketRepository.findByStatus(status);
        return tickets.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // Get tickets by user ID and status
    public List<TicketDTO> getTicketsByUserIdAndStatus(Long userId, TicketStatus status) {
        List<Ticket> tickets = ticketRepository.findByUserIdAndStatus(userId, status);
        return tickets.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // Get tickets by event ID and status
    public List<TicketDTO> getTicketsByEventIdAndStatus(Long eventId, TicketStatus status) {
        List<Ticket> tickets = ticketRepository.findByEventIdAndStatus(eventId, status);
        return tickets.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // Get expired tickets
    public List<TicketDTO> getExpiredTickets() {
        List<Ticket> tickets = ticketRepository.findExpiredTickets(LocalDateTime.now());
        return tickets.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // Count tickets by event ID and status
    public long countTicketsByEventIdAndStatus(Long eventId, TicketStatus status) {
        return ticketRepository.countByEventIdAndStatus(eventId, status);
    }
    
    // Generate unique ticket number
    private String generateTicketNumber() {
        return "TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    // Convert Ticket entity to DTO
    private TicketDTO convertToDTO(Ticket ticket) {
        return new TicketDTO(
            ticket.getId(),
            ticket.getTicketNumber(),
            ticket.getEventId(),
            ticket.getUserId(),
            ticket.getSeatNumbers(),
            ticket.getTicketType(),
            ticket.getPrice(),
            ticket.getStatus(),
            ticket.getPurchaseDate(),
            ticket.getEventDate(),
            ticket.getQrCode(),
            ticket.getVenueName(),
            ticket.getEventName()
        );
    }
}
