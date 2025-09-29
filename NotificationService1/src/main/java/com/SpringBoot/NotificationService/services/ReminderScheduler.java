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


    // @Scheduled(fixedRate = 60000, initialDelay = 30000)
@Scheduled(cron = "${reminder.check.interval}")
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
                String subject = "🎟️ Event Reminder: " + ticket.getEvent_name();

String body = "<div style='font-family: Arial, sans-serif; color: #333; line-height: 1.6;'>" +
                "<div style='background-color: #4CAF50; padding: 15px; text-align: center; color: white; border-radius: 8px 8px 0 0;'>" +
                    "<h2 style='margin: 0;'>Event Reminder</h2>" +
                "</div>" +
                
                "<div style='padding: 20px; border: 1px solid #ddd; border-top: none; border-radius: 0 0 8px 8px; background-color: #f9f9f9;'>" +
                    "<p style='font-size: 16px;'>Hello,</p>" +
                    "<p style='font-size: 15px;'>This is a friendly reminder for your upcoming event happening <b>tomorrow</b>.</p>" +
                    
                    "<table style='width: 100%; border-collapse: collapse; margin-top: 15px;'>" +
                        "<tr>" +
                            "<td style='padding: 8px; font-weight: bold; width: 120px;'>🎤 Event:</td>" +
                            "<td style='padding: 8px;'>" + ticket.getEvent_name() + "</td>" +
                        "</tr>" +
                        "<tr style='background-color: #fff;'>" +
                            "<td style='padding: 8px; font-weight: bold;'>📍 Venue:</td>" +
                            "<td style='padding: 8px;'>" + ticket.getVenue_name() + "</td>" +
                        "</tr>" +
                        "<tr>" +
                            "<td style='padding: 8px; font-weight: bold;'>🗓 Date & Time:</td>" +
                            "<td style='padding: 8px;'>" + ticket.getEventDate() + "</td>" +
                        "</tr>" +
                        "<tr style='background-color: #fff;'>" +
                            "<td style='padding: 8px; font-weight: bold;'>💺 Seats:</td>" +
                            "<td style='padding: 8px;'>" + ticket.getSeatNumbers().replace(",", ", ") + "</td>" +
                        "</tr>" +
                    "</table>" +
                    
                    "<p style='margin-top: 20px; font-size: 14px; color: #555;'>Please make sure to arrive at least 15 minutes before the event starts. We look forward to seeing you!</p>" +
                    
                    "<div style='margin-top: 25px; text-align: center;'>" +
                    "</div>" +
                "</div>" +
            "</div>";
                emailService.sendHtmlEmail(userEmail, subject, body);
                System.out.println("✅ Reminder sent to " + userEmail);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
