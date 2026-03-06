package com.revconnect.userservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "creator_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(length = 500)
    private String creatorBio;

    private String niche;

    private String contentCategory;

    private Boolean verified = false;
}