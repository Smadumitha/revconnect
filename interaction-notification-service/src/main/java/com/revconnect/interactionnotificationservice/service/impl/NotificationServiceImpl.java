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
    public Long getUnreadCount(Long userId){
        return notificationRepository.countByReceiverIdAndIsReadFalse(userId);
    }
    public void markAllAsRead(Long userId){

        List<Notification> notifications =
                notificationRepository.findByReceiverIdOrderByCreatedAtDesc(userId);

        notifications.forEach(n -> n.setIsRead(true));

        notificationRepository.saveAll(notifications);
    }

}