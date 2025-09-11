package com.event.TicketService.controller;

import com.event.TicketService.dto.CreateTicketRequest;
import com.event.TicketService.dto.TicketDTO;
import com.event.TicketService.model.TicketStatus;
import com.event.TicketService.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
// @CrossOrigin(origins = "*")
public class TicketController {
    
    @Autowired
    private TicketService ticketService;
    
    // Create a new ticket
    @PostMapping
public ResponseEntity<Map<String, Object>> createTicket(@RequestBody CreateTicketRequest request) {
    try {
        System.out.println("Received ticket request: " + request);
        TicketDTO ticket = ticketService.createTicket(request);

        Map<String, Object> response = Map.of(
            "ticketId", ticket.getId(),
            "qrCode", ticket.getQrCode()   // <-- Base64 from DB
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}


    
    // Get ticket by ID
    @GetMapping("/{id}")
    public ResponseEntity<TicketDTO> getTicketById(@PathVariable Long id) {
        try {
            TicketDTO ticket = ticketService.getTicketById(id);
            return new ResponseEntity<>(ticket, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    

    
    // Get all tickets by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TicketDTO>> getTicketsByUserId(@PathVariable Long userId) {
        try {
            List<TicketDTO> tickets = ticketService.getTicketsByUserId(userId);
            return new ResponseEntity<>(tickets, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Get all tickets by event ID
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<TicketDTO>> getTicketsByEventId(@PathVariable Long eventId) {
        try {
            List<TicketDTO> tickets = ticketService.getTicketsByEventId(eventId);
            return new ResponseEntity<>(tickets, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Update ticket status
    @PutMapping("/{id}/status")
    public ResponseEntity<TicketDTO> updateTicketStatus(
            @PathVariable Long id, 
            @RequestParam TicketStatus status) {
        try {
            TicketDTO ticket = ticketService.updateTicketStatus(id, status);
            return new ResponseEntity<>(ticket, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    // Confirm ticket
    @PutMapping("/{id}/confirm")
    public ResponseEntity<TicketDTO> confirmTicket(@PathVariable Long id) {
        try {
            TicketDTO ticket = ticketService.confirmTicket(id);
            return new ResponseEntity<>(ticket, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    // Cancel ticket
    @PutMapping("/{id}/cancel")
    public ResponseEntity<TicketDTO> cancelTicket(@PathVariable Long id) {
        try {
            TicketDTO ticket = ticketService.cancelTicket(id);
            return new ResponseEntity<>(ticket, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    // Mark ticket as used
    @PutMapping("/{id}/use")
    public ResponseEntity<TicketDTO> markTicketAsUsed(@PathVariable Long id) {
        try {
            TicketDTO ticket = ticketService.markTicketAsUsed(id);
            return new ResponseEntity<>(ticket, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    // Refund ticket
    @PutMapping("/{id}/refund")
    public ResponseEntity<TicketDTO> refundTicket(@PathVariable Long id) {
        try {
            TicketDTO ticket = ticketService.refundTicket(id);
            return new ResponseEntity<>(ticket, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    // Delete ticket
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        try {
            ticketService.deleteTicket(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    // Get tickets by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TicketDTO>> getTicketsByStatus(@PathVariable TicketStatus status) {
        try {
            List<TicketDTO> tickets = ticketService.getTicketsByStatus(status);
            return new ResponseEntity<>(tickets, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Get tickets by user ID and status
    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<List<TicketDTO>> getTicketsByUserIdAndStatus(
            @PathVariable Long userId, 
            @PathVariable TicketStatus status) {
        try {
            List<TicketDTO> tickets = ticketService.getTicketsByUserIdAndStatus(userId, status);
            return new ResponseEntity<>(tickets, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Get tickets by event ID and status
    @GetMapping("/event/{eventId}/status/{status}")
    public ResponseEntity<List<TicketDTO>> getTicketsByEventIdAndStatus(
            @PathVariable Long eventId, 
            @PathVariable TicketStatus status) {
        try {
            List<TicketDTO> tickets = ticketService.getTicketsByEventIdAndStatus(eventId, status);
            return new ResponseEntity<>(tickets, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
}
