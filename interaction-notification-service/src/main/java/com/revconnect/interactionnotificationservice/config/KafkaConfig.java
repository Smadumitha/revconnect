package com.revconnect.interactionnotificationservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic interactionTopic() {
        return new NewTopic("interaction-events", 1, (short) 1);
    }

    @Bean
    public NewTopic notificationTopic() {
        return new NewTopic("notification-events", 1, (short) 1);
    }
}
