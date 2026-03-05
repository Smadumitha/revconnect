package com.revconnect.interactionnotificationservice.service;

import com.revconnect.interactionnotificationservice.entity.Notification;

import java.util.List;

public interface NotificationService {

    void createNotification(Long receiverId, Long senderId, String type, String message);

    List<Notification> getUserNotifications(Long userId);

    void markAsRead(Long notificationId);
}