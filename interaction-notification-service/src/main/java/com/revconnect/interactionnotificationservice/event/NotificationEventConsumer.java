package com.revconnect.interactionnotificationservice.event;

import com.revconnect.interactionnotificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "notification-events", groupId = "notification-group")
    public void consumeNotificationEvent(String message) {
        System.out.println("Received external notification event: " + message);
        // Note: For real implementation, parse the DTO and call
        // notificationService.createNotification
    }
}
