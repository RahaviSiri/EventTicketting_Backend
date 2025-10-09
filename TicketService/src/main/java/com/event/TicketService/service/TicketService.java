package com.event.TicketService.service;

import com.event.TicketService.dto.CreateTicketRequest;
import com.event.TicketService.dto.TicketDTO;
import com.event.TicketService.model.Ticket;
import com.event.TicketService.model.TicketStatus;
import com.event.TicketService.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
        ticket.setPrice(request.getPrice());
        ticket.setStatus(TicketStatus.CONFIRMED);
        ticket.setPurchaseDate(LocalDateTime.now());
        ticket.setEventDate(request.getEventDate());
        ticket.setVenue_name(request.getVenueName());
        ticket.setEvent_name(request.getEventName());

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

    // // Get expired tickets
    // public List<TicketDTO> getExpiredTickets() {
    // List<Ticket> tickets =
    // ticketRepository.findExpiredTickets(LocalDateTime.now());
    // return tickets.stream()
    // .map(this::convertToDTO)
    // .collect(Collectors.toList());
    // }

    // // Count tickets by event ID and status
    // public long countTicketsByEventIdAndStatus(Long eventId, TicketStatus status)
    // {
    // return ticketRepository.countByEventIdAndStatus(eventId, status);
    // }

    // Generate unique ticket number
    private String generateTicketNumber() {
        return "TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // Convert Ticket entity to DTO
    private TicketDTO convertToDTO(Ticket ticket) {
        return TicketDTO.builder()
                .id(ticket.getId())
                .ticketNumber(ticket.getTicketNumber())
                .eventId(ticket.getEventId())
                .userId(ticket.getUserId())
                .seatNumbers(ticket.getSeatNumbers())
                .price(ticket.getPrice())
                .status(ticket.getStatus())
                .purchaseDate(ticket.getPurchaseDate())
                .qrCode(ticket.getQrCode())
                .event_name(ticket.getEvent_name())
                .venue_name(ticket.getVenue_name())
                .eventDate(ticket.getEventDate())
                .build();
    }

    public Double getRevenueByEventIds(List<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty())
            return 0.0;
        return ticketRepository.findRevenueByEventIds(eventIds);
    }

    public Long getTicketsByEventsIds(List<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty())
            return 0L;
        return ticketRepository.countTicketsByEventIds(eventIds);
    }

    public Double getRevenueByEventId(Long eventId) {
        return ticketRepository.findRevenueByEventId(eventId);
    }

    public Long getTicketsSoldByEvent(Long eventId) {
        return ticketRepository.countTicketsByEvent(eventId);
    }

    // Get tickets for events happening within the next 24 hours
    public List<TicketDTO> getTicketsForUpcomingEvents() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next24 = now.plusHours(24);
        List<com.event.TicketService.model.Ticket> tickets = ticketRepository.findByEventDateBetween(now, next24);
        return tickets.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Get revenue for a specific event for a specific month
    public List<Double> getAnnualRevenueForEvent(Long eventId, int year) {
        List<Double> monthlyRevenues = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            // Get the start and end date for the current month
            LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0, 0, 0);
            LocalDateTime endDate = startDate.withDayOfMonth(startDate.toLocalDate().lengthOfMonth()).withHour(23)
                    .withMinute(59).withSecond(59);

            // Call the repository method with the calculated start and end date
            Double revenue = ticketRepository.findMonthlyRevenueForEvent(eventId, startDate, endDate);
            monthlyRevenues.add(revenue); // Add revenue for the month to the list
        }

        return monthlyRevenues;
    }

}
