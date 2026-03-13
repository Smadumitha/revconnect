package com.revconnect.interactionnotificationservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "interactions",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"userId", "postId", "type"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Interaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long postId;

    private String type; // LIKE

    private LocalDateTime createdAt;
}