package com.SpringBoot.NotificationService.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.SpringBoot.NotificationService.dto.TicketDTO;

@Service
public class ReminderScheduler {

    @Autowired
    private EmailService emailService;

    @Autowired
    private TicketServiceClient ticketServiceClient; // to fetch tickets/events
    
    @Autowired
    private UserServiceClient userServiceClient;

    // // Run every 1 hour
    // @Scheduled(fixedRateString = "${reminder.check.interval}")
    @Scheduled(fixedRate = 60000, initialDelay = 30000)
    public void sendReminders() {
        try {
            // 1. Fetch all tickets for upcoming events
            List<TicketDTO> tickets = ticketServiceClient.getTicketsForUpcomingEvents();

            for (TicketDTO ticket : tickets) {
                // resolve user email from user service
                String userEmail = null;
                System.out.println("Processing ticket id=" + ticket.getId() + " for userId=" + ticket.getUserId());
                try {
                    if (ticket.getUserId() != null) {
                        var user = userServiceClient.getUserById(ticket.getUserId());
                        if (user != null) userEmail = user.getEmail();
                    }
                } catch(Exception e) {
                    // log and continue - can't find email
                    e.printStackTrace();
                }

                if (userEmail == null || userEmail.isBlank()) {
                    System.out.println("No email for ticket id=" + ticket.getId() + ", skipping reminder");
                    continue;
                }

                // 3. Send reminder email
                String subject = "Event Reminder: " + ticket.getEvent_name();
                String body = "<h2>Reminder: Your event is tomorrow!</h2>" +
                              "<p><b>Event:</b> " + ticket.getEvent_name() + "<br>" +
                              "<b>Venue:</b> " + ticket.getVenue_name() + "<br>" +
                              "<b>Date & Time:</b> " + ticket.getEventDate() + "<br>" +
                              "<b>Seats:</b> " + ticket.getSeatNumbers().replace(",", ", ") + "</p>";

                emailService.sendHtmlEmail(userEmail, subject, body);
                System.out.println("✅ Reminder sent to " + userEmail);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
