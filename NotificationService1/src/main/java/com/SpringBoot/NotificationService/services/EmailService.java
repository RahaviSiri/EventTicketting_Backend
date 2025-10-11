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
            qrImgHtml = "<div style='text-align:center; margin-top:20px;'>" +
                        "<p style='font-size:14px; color:#555;'>Scan your QR code at the entrance:</p>" +
                        "<img src='cid:qrCode' alt='QR Code' style='max-width:200px; border:1px solid #ddd; padding:5px; border-radius:8px;'/>" +
                        "</div>";
        }
    } catch (Exception e) {
        logger.error("Error decoding QR image", e);
        imageBytes = null;
        qrImgHtml = "";
    }

    String body = "<div style='font-family: Arial, sans-serif; color: #333; line-height: 1.6;'>" +
                    "<div style='background-color:#1976d2; padding:15px; text-align:center; color:white; border-radius:8px 8px 0 0;'>" +
                        "<h2 style='margin:0;'>🎟 Ticket Confirmation</h2>" +
                    "</div>" +

                    "<div style='padding:20px; border:1px solid #ddd; border-top:none; border-radius:0 0 8px 8px; background-color:#f9f9f9;'>" +
                        "<p style='font-size:16px;'>Thank you for booking! Your ticket is confirmed.</p>" +

                        "<table style='width:100%; border-collapse:collapse; margin-top:15px; font-size:15px;'>" +
                            "<tr>" +
                                "<td style='padding:8px; font-weight:bold; width:130px;'>📌 Order ID:</td>" +
                                "<td style='padding:8px;'>" + ticket.getId() + "</td>" +
                            "</tr>" +
                            "<tr style='background-color:#fff;'>" +
                                "<td style='padding:8px; font-weight:bold;'>🎤 Event:</td>" +
                                "<td style='padding:8px;'>" + ticket.getEventName() + "</td>" +
                            "</tr>" +
                            "<tr>" +
                                "<td style='padding:8px; font-weight:bold;'>📍 Venue:</td>" +
                                "<td style='padding:8px;'>" + ticket.getVenueName() + "</td>" +
                            "</tr>" +
                            "<tr style='background-color:#fff;'>" +
                                "<td style='padding:8px; font-weight:bold;'>🗓 Date:</td>" +
                                "<td style='padding:8px;'>" + ticket.getEventDate() + "</td>" +
                            "</tr>" +
                            "<tr>" +
                                "<td style='padding:8px; font-weight:bold;'>💺 Seats:</td>" +
                                "<td style='padding:8px;'>" + ticket.getSeatNumbers().replace(",", ", ") + "</td>" +
                            "</tr>" +
                            "<tr style='background-color:#fff;'>" +
                                "<td style='padding:8px; font-weight:bold;'>💵 Price:</td>" +
                                "<td style='padding:8px;'>$" + ticket.getPrice() + "</td>" +
                            "</tr>" +
                        "</table>" +

                        qrImgHtml +

                        "<p style='margin-top:20px; font-size:14px; color:#555;'>Please keep this email safe and show your QR code at the entrance.</p>" +

                        "<div style='margin-top:25px; text-align:center;'>" +
                        "</div>" +
                    "</div>" +
                "</div>";

    logger.info("Preparing email subject='{}' to={} bodyLength={}", subject, to, body.length());
    logger.debug("Email body: {}", body);

    helper.setText(body, true); // true = HTML content

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
