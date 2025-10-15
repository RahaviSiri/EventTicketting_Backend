package com.SpringBoot.NotificationService.services;

import com.SpringBoot.NotificationService.dto.TicketDTO;
import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.core.io.ByteArrayResource;

import java.io.IOException;
import java.util.Base64;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Value("${SENDGRID_API_KEY}")
    private String sendGridApiKey;

    @Value("${spring.mail.from:rahavi24siri@gmail.com}")
    private String fromEmail;

    /**
     * Send a rich HTML ticket email (with embedded QR and details)
     */
    public void sendTicketEmail(String to, String subject, TicketDTO ticket) throws Exception {
        logger.info("Preparing ticket email for {}", to);

        // ✅ Build the email content (same design as your previous code)
        String qrImgHtml = "";
        byte[] imageBytes = null;

        try {
            String qrCodeBase64 = ticket.getQrCode();
            if (qrCodeBase64 != null && !qrCodeBase64.isBlank()) {
                String base64 = qrCodeBase64.trim();
                if (base64.startsWith("data:")) {
                    int comma = base64.indexOf(',');
                    if (comma > -1)
                        base64 = base64.substring(comma + 1);
                }
                imageBytes = Base64.getDecoder().decode(base64);
                logger.info("Decoded QR image size: {} bytes", imageBytes.length);
                qrImgHtml = "<div style='text-align:center; margin-top:20px;'>" +
                        "<p style='font-size:14px; color:#555;'>Scan your QR code at the entrance:</p>" +
                        "<img src='cid:qrcode' alt='QR Code' style='max-width:200px; border:1px solid #ddd; padding:5px; border-radius:8px;'/>" +
                        "</div>";
            }
        } catch (Exception e) {
            logger.error("Error decoding QR image", e);
        }

        String body = "<div style='font-family: Arial, sans-serif; color: #333; line-height: 1.6;'>" +
                "<div style='background-color:#1976d2; padding:15px; text-align:center; color:white; border-radius:8px 8px 0 0;'>" +
                "<h2 style='margin:0;'>🎟 Ticket Confirmation</h2>" +
                "</div>" +
                "<div style='padding:20px; border:1px solid #ddd; border-top:none; border-radius:0 0 8px 8px; background-color:#f9f9f9;'>" +
                "<p style='font-size:16px;'>Thank you for booking! Your ticket is confirmed.</p>" +
                "<table style='width:100%; border-collapse:collapse; margin-top:15px; font-size:15px;'>" +
                "<tr><td style='padding:8px; font-weight:bold;'>📌 Order ID:</td><td style='padding:8px;'>" + ticket.getId() + "</td></tr>" +
                "<tr style='background-color:#fff;'><td style='padding:8px; font-weight:bold;'>🎤 Event:</td><td style='padding:8px;'>" + ticket.getEventName() + "</td></tr>" +
                "<tr><td style='padding:8px; font-weight:bold;'>📍 Venue:</td><td style='padding:8px;'>" + ticket.getVenueName() + "</td></tr>" +
                "<tr style='background-color:#fff;'><td style='padding:8px; font-weight:bold;'>🗓 Date:</td><td style='padding:8px;'>" + ticket.getEventDate() + "</td></tr>" +
                "<tr><td style='padding:8px; font-weight:bold;'>💺 Seats:</td><td style='padding:8px;'>" + ticket.getSeatNumbers().replace(",", ", ") + "</td></tr>" +
                "<tr style='background-color:#fff;'><td style='padding:8px; font-weight:bold;'>💵 Price:</td><td style='padding:8px;'>$" + ticket.getPrice() + "</td></tr>" +
                "</table>" + qrImgHtml +
                "<p style='margin-top:20px; font-size:14px; color:#555;'>Please keep this email safe and show your QR code at the entrance.</p>" +
                "</div></div>";

        sendEmailViaSendGrid(to, subject, body, imageBytes);
    }

    /**
     * Send simple HTML email (no attachments)
     */
    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        sendEmailViaSendGrid(to, subject, htmlBody, null);
    }

    /**
     * Generic SendGrid mail sender
     */
    private void sendEmailViaSendGrid(String to, String subject, String htmlContent, byte[] attachmentBytes) {
        Email from = new Email(fromEmail);
        Email toEmail = new Email(to);
        Content content = new Content("text/html", htmlContent);
        Mail mail = new Mail(from, subject, toEmail, content);

        // Add attachment if QR image exists
        if (attachmentBytes != null) {
            Attachments attachment = new Attachments();
            String base64Encoded = Base64.getEncoder().encodeToString(attachmentBytes);
            attachment.setContent(base64Encoded);
            attachment.setType("image/png");
            attachment.setFilename("qrcode.png");
            attachment.setDisposition("attachment");
            mail.addAttachments(attachment);
        }

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            logger.info("📨 SendGrid API response: {} {}", response.getStatusCode(), response.getBody());
        } catch (IOException e) {
            logger.error("❌ Failed to send email via SendGrid API", e);
            throw new RuntimeException("Failed to send email via SendGrid", e);
        }
    }
}
