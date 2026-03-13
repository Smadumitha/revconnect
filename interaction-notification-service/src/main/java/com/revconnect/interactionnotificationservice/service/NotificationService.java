package com.revconnect.interactionnotificationservice.service;

import com.revconnect.interactionnotificationservice.entity.Notification;
import com.revconnect.interactionnotificationservice.dto.NotificationPreferencesDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {

    void createNotification(Long receiverId, Long senderId, String type, String message);

    List<Notification> getUserNotifications(Long userId);

    void markAsRead(Long notificationId);
    
    Long getUnreadCount(Long userId);
    
    void markAllAsRead(Long userId);
    
    Page<Notification> getUserNotificationsPaged(Long userId, Pageable pageable);
    
    NotificationPreferencesDTO getPreferences(Long userId);
    
    NotificationPreferencesDTO updatePreferences(Long userId, NotificationPreferencesDTO prefs);
}