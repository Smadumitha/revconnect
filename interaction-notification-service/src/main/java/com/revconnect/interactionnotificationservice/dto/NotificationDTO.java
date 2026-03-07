package com.revconnect.interactionnotificationservice.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {

    private Long id;
    private Long receiverId;
    private Long senderId;
    private String type;
    private String message;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
