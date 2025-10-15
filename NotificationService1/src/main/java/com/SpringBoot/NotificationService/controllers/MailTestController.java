package com.SpringBoot.NotificationService.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SpringBoot.NotificationService.services.EmailService;

@RestController
public class MailTestController {
    @Autowired EmailService emailService;

    @GetMapping("/test-mail")
    public String sendTestMail() {
        try {
            emailService.sendHtmlEmail("rahavi24siri@gmail.com", "Render Gmail Test", "<h3>✅ Mail working!</h3>");
            return "Mail sent successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Mail failed: " + e.getMessage();
        }
    }
}
