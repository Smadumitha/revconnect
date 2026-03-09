package com.revconnect.userservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String username;

    private String displayName;     // NEW — shown in UI

    private String email;           // NEW — synced from auth-service

    private String role;            // NEW — PERSONAL, CREATOR, BUSINESS

    @Column(length = 500)
    private String bio;

    private String location;

    private String website;

    private String profilePictureUrl;

    private Boolean isPrivate = false;

    // Extended profile fields
    private String category;        // NEW — for creators
    private String industry;        // NEW — for business
    private String businessAddress; // NEW
    private String businessHours;   // NEW
    private String contactEmail;    // NEW

    private LocalDateTime createdAt; // NEW

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (displayName == null) displayName = username;
    }
}