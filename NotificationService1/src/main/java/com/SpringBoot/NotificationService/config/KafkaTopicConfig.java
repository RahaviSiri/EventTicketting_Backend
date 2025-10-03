package com.SpringBoot.NotificationService.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration

public class KafkaTopicConfig {

    @Bean
    public NewTopic orderConfirmedTopic() {
        return TopicBuilder.name("order-confirmed").build();
    }

    @Bean
    public NewTopic eventUpdatedTopic() {
        return TopicBuilder.name("event-updated").build();
    }

    @Bean
    public NewTopic eventCancelledTopic() {
        return TopicBuilder.name("event-cancelled").build();
    }

    @Bean
    public NewTopic reminderTasksTopic() {
        return TopicBuilder.name("reminder-tasks").build();
    }

    @Bean
    public NewTopic contactMessagesTopic() {
        return TopicBuilder.name("contact-messages").build();
    }

}
