package com.SpringBoot.OrderService.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final String ORDER_CONFIRMED_TOPIC = "order-confirmed";

    public void sendOrderConfirmed(String message) {
        kafkaTemplate.send(ORDER_CONFIRMED_TOPIC, message);
    }
}
