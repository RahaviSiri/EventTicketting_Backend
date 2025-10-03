package com.SpringBoot.NotificationService.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.SpringBoot.NotificationService.dto.TicketDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;

@Service
public class NotificationConsumer {

    @Autowired
    private EmailService emailService;

    @Value("${spring.mail.username}")
    private String supportEmail;

    @Autowired
    private TicketServiceClient ticketServiceClient;

    @Autowired
    private UserServiceClient userServiceClient;

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
        System.out.println("Received event update message: " + message);
        try {
            JsonNode node = objectMapper.readTree(message);
            Long eventId = node.has("eventId") ? node.get("eventId").asLong() : null;
            String note = node.has("note") ? node.get("note").asText() : "Event details have changed.";

            if (eventId == null) {
                System.err.println("eventId not found in message: " + message);
                return;
            }

            // Fetch tickets for the event
            System.out.println("Fetching tickets for eventId=" + eventId);
            java.util.List<TicketDTO> tickets = ticketServiceClient.getTicketsByEventId(eventId);

            if (tickets == null || tickets.isEmpty()) {
                System.out.println("No tickets found for eventId=" + eventId);
                return;
            }

            // For each ticket, fetch user and send email
            for (TicketDTO ticket : tickets) {
                try {
                    Long userId = ticket.getUserId();
                    String toEmail = null;
                    if (userId != null) {
                        try {
                            com.SpringBoot.NotificationService.dto.UserDTO user = userServiceClient.getUserById(userId);
                            if (user != null)
                                toEmail = user.getEmail();
                        } catch (Exception fe) {
                            System.err.println("Failed to fetch user " + userId + ": " + fe.getMessage());
                        }
                    }

                    if (toEmail == null || toEmail.isBlank()) {
                        System.err.println("No email for userId=" + userId + "; skipping");
                        continue;
                    }

                    String subject = "📢 Event Update: " + ticket.getEvent_name();

                    // Styled HTML email body
                    String html = "<div style='font-family: Arial, sans-serif; color: #333; line-height: 1.6;'>" +
                            "<div style='background-color: #FF9800; padding: 15px; text-align: center; color: white; border-radius: 8px 8px 0 0;'>"
                            +
                            "<h2 style='margin: 0;'>Event Updated</h2>" +
                            "</div>" +

                            "<div style='padding: 20px; border: 1px solid #ddd; border-top: none; border-radius: 0 0 8px 8px; background-color: #fdfdfd;'>"
                            +
                            "<p style='font-size: 15px;'>Hello,</p>" +
                            "<p style='font-size: 15px;'>" + note + "</p>" +

                            "<table style='width: 100%; border-collapse: collapse; margin-top: 15px;'>" +
                            "<tr>" +
                            "<td style='padding: 8px; font-weight: bold; width: 120px;'>🎤 Event:</td>" +
                            "<td style='padding: 8px;'>" + ticket.getEvent_name() + "</td>" +
                            "</tr>" +
                            "<tr style='background-color: #fff;'>" +
                            "<td style='padding: 8px; font-weight: bold;'>📍 Venue:</td>" +
                            "<td style='padding: 8px;'>"
                            + (ticket.getVenue_name() == null ? "" : ticket.getVenue_name()) + "</td>" +
                            "</tr>" +
                            "<tr>" +
                            "<td style='padding: 8px; font-weight: bold;'>🗓 Date & Time:</td>" +
                            "<td style='padding: 8px;'>" + (ticket.getEventDate() == null ? "" : ticket.getEventDate())
                            + "</td>" +
                            "</tr>" +
                            "<tr style='background-color: #fff;'>" +
                            "<td style='padding: 8px; font-weight: bold;'>💺 Your Seats:</td>" +
                            "<td style='padding: 8px;'>"
                            + (ticket.getSeatNumbers() == null ? "" : ticket.getSeatNumbers().replace(",", ", "))
                            + "</td>" +
                            "</tr>" +
                            "</table>" +

                            "<p style='margin-top: 20px; font-size: 14px; color: #555;'>If the update affects your ticket (time/venue), we’ll contact you with the next steps.</p>"
                            +

                            "<div style='margin-top: 25px; text-align: center;'>" +
                            "</div>" +
                            "</div>" +
                            "</div>";

                    // Send styled HTML email
                    emailService.sendHtmlEmail(toEmail, subject, html);

                    System.out.println("Sent event-update email to " + toEmail + " for ticket " + ticket.getId());

                } catch (Exception inner) {
                    inner.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = "contact-messages", groupId = "notification-service")
    public void handleContactMessage(String message) {
        try {
            System.out.println("Received contact message from Kafka: " + message);

            JsonNode node = new ObjectMapper().readTree(message);
            String name = node.has("name") ? node.get("name").asText() : "Anonymous";
            String email = node.has("email") ? node.get("email").asText() : "";
            String subject = node.has("subject") ? node.get("subject").asText() : "No Subject";
            String msg = node.has("message") ? node.get("message").asText() : "";

            if (email.isBlank()) {
                System.err.println("No email provided, skipping message.");
                return;
            }

            // Construct HTML email
            String html = "<div style='font-family: Arial, sans-serif; line-height: 1.6;'>" +
                    "<h2>New Contact Us Message</h2>" +
                    "<p><strong>Name:</strong> " + name + "</p>" +
                    "<p><strong>Email:</strong> " + email + "</p>" +
                    "<p><strong>Subject:</strong> " + subject + "</p>" +
                    "<p><strong>Message:</strong><br/>" + msg + "</p>" +
                    "</div>";

            // Send email to the support address configured in spring.mail.username
            emailService.sendHtmlEmail(supportEmail, "New Contact Us Message: " + subject, html);

            System.out.println("✅ Sent contact-us email to " + supportEmail);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Failed to process contact message: " + message);
        }
    }

}
