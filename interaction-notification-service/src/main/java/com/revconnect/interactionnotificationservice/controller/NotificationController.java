package com.revconnect.interactionnotificationservice.controller;

import com.revconnect.interactionnotificationservice.entity.Notification;
import com.revconnect.interactionnotificationservice.service.impl.NotificationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationServiceImpl notificationService;

    @GetMapping("/{userId}")
    public List<Notification> getUserNotifications(@PathVariable Long userId) {
        return notificationService.getUserNotifications(userId);
    }

    @PutMapping("/read/{notificationId}")
    public String markAsRead(@PathVariable Long notificationId) {

        notificationService.markAsRead(notificationId);

        return "Notification marked as read";
    }
}