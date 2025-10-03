package com.SpringBoot.NotificationService.controllers;

import com.SpringBoot.NotificationService.services.ContactKafkaProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class ContactController {

    @Autowired
    private ContactKafkaProducer producer;

    @PostMapping("/contact")
    public ResponseEntity<String> sendContactMessage(@RequestBody Map<String, String> body) {
        System.out.println(body);
        try {
            String json = new ObjectMapper().writeValueAsString(body);
            producer.sendMessage(json);
            return ResponseEntity.ok("Message sent to Kafka");
        } catch (Exception e) {
            e.printStackTrace(); // full stack trace
            System.err.println("Kafka send failed: " + e.getMessage());
            return ResponseEntity.status(500).body("Failed to send message: " + e.getMessage());
        }
    }

}
