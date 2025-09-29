package com.SpringBoot.NotificationService.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.SpringBoot.NotificationService.dto.TicketDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class NotificationConsumer {

    @Autowired
    private EmailService emailService;

    @Autowired
    private TicketServiceClient ticketServiceClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "order-confirmed", groupId = "notification-service")
    public void handleOrderConfirmed(String message) {
        try {
            // Parse JSON
            JsonNode node = objectMapper.readTree(message);

            Long ticketId = node.get("ticketId").asLong();
            String userEmail = node.get("userEmail").asText();

            // Fetch ticket details from Ticket Service
            TicketDTO ticket = ticketServiceClient.getTicketById(ticketId);
            System.out.println("TicketDTO fetched: " + ticket);

            // Send email (HTML with QR code)
            String subject = "Your E-Ticket Confirmation";
            emailService.sendTicketEmail(userEmail, subject, ticket);

            System.out.println("✅ Sent confirmation email to " + userEmail);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Failed to process Kafka message: " + message);
        }
    }

    @KafkaListener(topics = "event-updated", groupId = "notification-service")
    public void handleEventUpdated(String message) {
        System.out.println("Received event update: " + message);
        // You can create a simpler HTML email if needed
        try {
            TicketDTO dummy = TicketDTO.builder()
                                       .event_name("Event Update")
                                       .seatNumbers("")
                                       .qrCode("")
                                       .venue_name("")
                                       .build();
            emailService.sendTicketEmail("user@example.com", "Event Update", dummy);
        } catch(Exception e) { e.printStackTrace(); }
    }

    @KafkaListener(topics = "event-cancelled", groupId = "notification-service")
    public void handleEventCancelled(String message) {
        try {
            TicketDTO dummy = TicketDTO.builder()
                                       .event_name("Event Cancelled")
                                       .seatNumbers("")
                                       .qrCode("")
                                       .venue_name("")
                                       .build();
            emailService.sendTicketEmail("user@example.com", "Event Cancelled", dummy);
        } catch(Exception e) { e.printStackTrace(); }
    }
}
