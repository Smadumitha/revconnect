package com.revconnect.interactionnotificationservice.service.impl;

import com.revconnect.interactionnotificationservice.entity.Notification;
import com.revconnect.interactionnotificationservice.repository.NotificationRepository;
import com.revconnect.interactionnotificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public void createNotification(Long receiverId, Long senderId, String type, String message) {

        Notification notification = Notification.builder()
                .receiverId(receiverId)
                .senderId(senderId)
                .type(type)
                .message(message)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByReceiverIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public void markAsRead(Long notificationId) {

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(
                        () -> new com.revconnect.interactionnotificationservice.exception.ResourceNotFoundException(
                                "Notification not found"));

        notification.setIsRead(true);

        notificationRepository.save(notification);
    }
    
    @Override
    public Long getUnreadCount(Long userId){
        return notificationRepository.countByReceiverIdAndIsReadFalse(userId);
    }
    
    @Override
    public void markAllAsRead(Long userId){

        List<Notification> notifications =
                notificationRepository.findByReceiverIdOrderByCreatedAtDesc(userId);

        notifications.forEach(n -> n.setIsRead(true));

        notificationRepository.saveAll(notifications);
    }

    @Override
    public org.springframework.data.domain.Page<Notification> getUserNotificationsPaged(Long userId, org.springframework.data.domain.Pageable pageable) {
        return notificationRepository.findByReceiverIdOrderByCreatedAtDesc(userId, pageable);
    }

    @Override
    public com.revconnect.interactionnotificationservice.dto.NotificationPreferencesDTO getPreferences(Long userId) {
        // Return default prefs for now
        return com.revconnect.interactionnotificationservice.dto.NotificationPreferencesDTO.builder()
            .connectionRequests(true)
            .postLikes(true)
            .postComments(true)
            .postShares(true)
            .newFollowers(true)
            .build();
    }

    @Override
    public com.revconnect.interactionnotificationservice.dto.NotificationPreferencesDTO updatePreferences(Long userId, com.revconnect.interactionnotificationservice.dto.NotificationPreferencesDTO prefs) {
        // Just return as-is for now since we don't have a DB table for preferences
        return prefs;
    }
}