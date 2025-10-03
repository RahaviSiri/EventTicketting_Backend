package com.SpringBoot.NotificationService.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ContactKafkaProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final String topic = "contact-messages";

    public void sendMessage(String messageJson) {
        kafkaTemplate.send(topic, messageJson);
    }
}
