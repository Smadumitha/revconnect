package com.revconnect.postfeedservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "posts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    @Column(length = 2000)
    private String content;
    private String mediaUrl;
    private String type;          // NEW — TEXT, IMAGE, SHARE, PROMOTIONAL
    private String status;        // NEW — PUBLISHED, DRAFT, SCHEDULED
    private Boolean promotional;
    private Boolean pinned;
    private String ctaText;
    private String ctaUrl;
    private LocalDateTime scheduledAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // REMOVED @ElementCollection to avoid conflict with standalone entities PostHashtag and ProductTag

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (type == null) type = "TEXT";
        if (status == null) {
            status = (scheduledAt != null) ? "SCHEDULED" : "PUBLISHED";
        }
        if (promotional == null) promotional = false;
        if (pinned == null) pinned = false;
    }
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}