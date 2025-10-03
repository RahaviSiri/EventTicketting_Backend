package com.SpringBoot.UserService.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserNotification {
    private static final String USER_SIGNUP_TOPIC = "user-created";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void publishUserCreated(String message) {
        kafkaTemplate.send(USER_SIGNUP_TOPIC, message);
    }
}

