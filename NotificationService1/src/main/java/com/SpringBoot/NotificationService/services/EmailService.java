package com.SpringBoot.NotificationService.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.SpringBoot.NotificationService.dto.TicketDTO;
import java.util.Base64;
import org.springframework.core.io.ByteArrayResource;

import jakarta.mail.internet.MimeMessage;
 


@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    public void sendTicketEmail(String to, String subject, TicketDTO ticket) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true); // true = multipart

        helper.setTo(to);
        helper.setSubject(subject);

  
        String qrImgHtml = "";
        byte[] imageBytes = null;
        try {
            String qrCodeBase64 = ticket.getQrCode();
            if (qrCodeBase64 != null && !qrCodeBase64.isBlank()) {
                String base64 = qrCodeBase64.trim();
                if (base64.startsWith("data:")) {
                    int comma = base64.indexOf(',');
                    if (comma > -1) base64 = base64.substring(comma + 1);
                }
                imageBytes = Base64.getDecoder().decode(base64);
                logger.info("Decoded QR image size: {} bytes", imageBytes.length);
                qrImgHtml = "<p>Scan your QR code at the entrance:</p><img src='cid:qrCode' alt='QR Code'/>";
            }
        } catch (Exception e) {
            logger.error("Error decoding QR image", e);
            imageBytes = null;
            qrImgHtml = "";
        }

        String body = "<h2>Thank you for booking! Your ticket is confirmed.</h2>" +
                      "<p><b>Order ID:</b> " + ticket.getId() + "<br>" +
                      "<b>Event:</b> " + ticket.getEvent_name() + "<br>" +
                      "<b>Venue:</b> " + ticket.getVenue_name() + "<br>" +
                      "<b>Date:</b> " + ticket.getEventDate() + "<br>" +
                      "<b>Seats:</b> " + ticket.getSeatNumbers().replace(",", ", ") + "<br>" +
                      "<b>Price:</b> $" + ticket.getPrice() + "</p>" +
                      qrImgHtml;

        // Log subject and body for debugging
        logger.info("Preparing email subject='{}' to={} bodyLength={}", subject, to, body.length());
        logger.debug("Email body: {}", body);

        helper.setText(body, true); // true = HTML content

        // Attach image (inline and as attachment) AFTER setting the HTML body
        if (imageBytes != null) {
            try {
                helper.addInline("qrCode", new ByteArrayResource(imageBytes), "image/png");
                logger.info("Added inline QR image (CID)");
            } catch (Exception inlineEx) {
                logger.warn("Failed to add inline QR image: {}", inlineEx.getMessage());
            }
            try {
                helper.addAttachment("qrcode.png", new ByteArrayResource(imageBytes), "image/png");
                logger.info("Added QR image as attachment");
            } catch (Exception attachEx) {
                logger.warn("Failed to add QR attachment: {}", attachEx.getMessage());
            }
        }

        mailSender.send(message);
    }

    // Send arbitrary HTML email
    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to send html email", e);
        }
    }
}
