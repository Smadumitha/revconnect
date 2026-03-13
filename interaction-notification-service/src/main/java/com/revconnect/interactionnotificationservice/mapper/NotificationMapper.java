package com.revconnect.interactionnotificationservice.mapper;

import com.revconnect.interactionnotificationservice.dto.NotificationDTO;
import com.revconnect.interactionnotificationservice.entity.Notification;

public class NotificationMapper {

    public static NotificationDTO toDTO(Notification notification) {
        if (notification == null) {
            return null;
        }

        return NotificationDTO.builder()
                .id(notification.getId())
                .receiverId(notification.getReceiverId())
                .senderId(notification.getSenderId())
                .type(notification.getType())
                .message(notification.getMessage())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }

    public static Notification toEntity(NotificationDTO dto) {
        if (dto == null) {
            return null;
        }

        return Notification.builder()
                .id(dto.getId())
                .receiverId(dto.getReceiverId())
                .senderId(dto.getSenderId())
                .type(dto.getType())
                .message(dto.getMessage())
                .isRead(dto.getIsRead())
                .createdAt(dto.getCreatedAt())
                .build();
    }
}
