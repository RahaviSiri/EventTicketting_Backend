package com.SpringBoot.NotificationService.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SpringBoot.NotificationService.services.EmailService;

@RestController
@RequestMapping("/api/test")
public class TestEmailController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/email")
    public ResponseEntity<String> sendTestEmail() {
        String to = "shaliakatanisha@gmail.com"; // replace or keep
        String subject = "Test email from NotificationService";
        String body = "<h3>This is a test email from NotificationService</h3><p>If you see this, SMTP is working.</p>";
        try {
            emailService.sendHtmlEmail(to, subject, body);
            return ResponseEntity.ok("Test email sent (attempted)");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to send test email: " + e.getMessage());
        }
    }
}
