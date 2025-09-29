package com.SpringBoot.EventServices.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EventNotificationProducer {

    private static final String EVENT_UPDATED_TOPIC = "event-updated";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void publishEventUpdated(String message) {
        kafkaTemplate.send(EVENT_UPDATED_TOPIC, message);
    }
}
