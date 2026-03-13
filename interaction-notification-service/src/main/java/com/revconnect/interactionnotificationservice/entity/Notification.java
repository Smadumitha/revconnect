package com.revconnect.interactionnotificationservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long receiverId; // RENAMED from userId to match DTO and logic

    private Long senderId;

    private String type;        // CONNECTION_REQUEST, POST_LIKED, etc.

    @Column(length = 500)
    private String message;

    private Long referenceId;   // postId or connectionId

    private Boolean isRead;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (isRead == null) isRead = false;
    }
}