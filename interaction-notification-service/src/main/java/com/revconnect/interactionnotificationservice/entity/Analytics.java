package com.revconnect.interactionnotificationservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "analytics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Analytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long postId;

    private LocalDate date;

    private Long likes;

    private Long comments;

    private Long shares;

    private Long newFollowers;

    private Long profileViews;
}