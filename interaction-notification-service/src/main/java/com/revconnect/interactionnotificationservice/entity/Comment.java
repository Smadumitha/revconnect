package com.revconnect.interactionnotificationservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long postId;

    @Column(nullable = false, length = 1000)
    private String content;

    private Long parentCommentId; // NULL = normal comment, otherwise reply

    private LocalDateTime createdAt;
}